package ktordiscord.core

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import ktordiscord.components.MessagePayload
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.ApplicationCommandData
import ktordiscord.components.interactions.Interaction
import ktordiscord.components.interactions.MessageComponentData
import ktordiscord.gateway.DiscordWebSocketSession
import ktordiscord.gateway.events.DispatchEvent
import kotlin.reflect.KClass

/**
 * Discord client.
 *
 * Build an instance with the suspending factory [DiscordClient.create]: construction performs
 * network I/O (it resolves the bot's application id), so it cannot happen in a plain constructor.
 *
 * @property token The application identification token.
 */
class DiscordClient private constructor(internal val token: String) {
    private val httpClientLogger = KotlinLogging.logger("HTTP_LOGGER")
    internal val interactionLogger = KotlinLogging.logger("INTERACTION_LOGGER")

    // Scope owning every coroutine spawned by this client (Gateway loop, heartbeat…).
    // Cancelled by [close] for a clean shutdown.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // The http client used to send requests
    internal var httpClient: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true

            })
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
                isLenient = true
            })
        }
        // Discord rate-limit handling: on a 429, wait the delay advertised by the response and
        // replay the request. We only retry on 429 (not 5xx / network errors — out of scope).
        install(HttpRequestRetry) {
            maxRetries = 5
            retryIf { _, response -> response.status.value == 429 }
            // `respectRetryAfterHeader = true` lets Ktor also honor `Retry-After` on its own; kept
            // as a safety net. We read it explicitly here to log the X-RateLimit-* context and to
            // fall back on `X-RateLimit-Reset-After` when the plain header is absent.
            delayMillis(respectRetryAfterHeader = true) { _ ->
                val headers = response?.headers
                // Discord's Retry-After is in (whole) seconds; X-RateLimit-Reset-After is fractional.
                val retryAfterMs = headers?.get(HttpHeaders.RetryAfter)?.toLongOrNull()?.times(1000)
                    ?: headers?.get("X-RateLimit-Reset-After")?.toDoubleOrNull()?.times(1000)?.toLong()
                    ?: 1000L
                // Defensive cap: never suspend a coroutine for an absurd duration.
                val delayMs = retryAfterMs.coerceAtMost(MAX_RATE_LIMIT_DELAY_MS)
                if (retryAfterMs > MAX_RATE_LIMIT_DELAY_MS) {
                    httpClientLogger.warn { "Rate-limit Retry-After ${retryAfterMs}ms exceeds cap, waiting ${delayMs}ms instead" }
                }
                httpClientLogger.warn {
                    "Rate limited (429), retrying in ${delayMs}ms " +
                        "[global=${headers?.get("X-RateLimit-Global")}, " +
                        "scope=${headers?.get("X-RateLimit-Scope")}, " +
                        "remaining=${headers?.get("X-RateLimit-Remaining")}]"
                }
                delayMs
            }
        }
    }

    // Backing flow for classic events received from Discord.
    // Buffered so a slow/absent consumer never suspends the Gateway loop.
    private val _events = MutableSharedFlow<DispatchEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Stream of classic events received from Discord.
    val events: SharedFlow<DispatchEvent> = _events.asSharedFlow()

    // Backing flow for interaction events.
    private val _interactions = MutableSharedFlow<Interaction>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // Stream of interaction events.
    val interactions: SharedFlow<Interaction> = _interactions.asSharedFlow()

    // the web socket session used to receive/send Gateway intents
    private val wssSession = DiscordWebSocketSession(httpClient, scope, _events, _interactions, token)

    // Interaction routing registries: application command name -> handler, component custom_id -> handler.
    private val commandHandlers = mutableMapOf<String, CommandHandler>()
    private val componentHandlers = mutableMapOf<String, ComponentHandler>()

    // Event routing registry: event class -> handlers registered for it (multiple listeners allowed,
    // invoked in registration order). Keyed by the concrete DispatchEvent subclass (all are leaf
    // data classes), so `event::class` matches exactly what `on<T>()` registered under T::class.
    private val eventHandlers = mutableMapOf<KClass<out DispatchEvent>, MutableList<suspend EventScope<DispatchEvent>.() -> Unit>>()

    // Internal dispatch loops: route every incoming interaction / event to its registered handler(s).
    // Each is run in its own child coroutine so a slow handler never blocks the others.
    // They read the public `interactions` / `events` SharedFlows, so a consumer can still collect
    // them in parallel.
    init {
        scope.launch {
            interactions.collect { interaction ->
                scope.launch { dispatchInteraction(interaction) }
            }
        }
        scope.launch {
            events.collect { event ->
                eventHandlers[event::class]?.forEach { handler ->
                    scope.launch { EventScope(event, this@DiscordClient).handler() }
                }
            }
        }
    }

    internal var apiVersion = 10
    internal var discordURL = "https://discord.com/api/v$apiVersion"

    internal lateinit var applicationId: String
    private lateinit var interactionManager: InteractionManager

    companion object {
        // Upper bound on how long a single rate-limited request will wait before replaying, so an
        // unexpectedly large Retry-After can never suspend a coroutine for an absurd duration.
        private const val MAX_RATE_LIMIT_DELAY_MS = 60_000L

        /**
         * Create and initialize a [DiscordClient].
         *
         * Resolves the bot's application id (a REST call), so a valid [token] and network access
         * are required. Prefer this over a constructor to keep instantiation non-blocking.
         *
         * @param token the bot application identification token.
         */
        suspend fun create(token: String): DiscordClient {
            val client = DiscordClient(token)
            client.applicationId = client.getMeApplicationId().value
            client.interactionManager = InteractionManager.create(client)
            return client
        }
    }

    //#region HTTP Calls
    /**
     * Send a message to a specific discord channel.
     *
     * Uses the same [ResponseScope] as interaction replies, so a plain channel message can carry
     * buttons whose click callback is bound inline with `button(...).click { }` — the callback is
     * routed by its `custom_id` like any component interaction, no command handler required.
     *
     * @param channelId the channel id to send a message
     * @param init the message builder function
     * @return the Http response from discord
     */
    suspend fun sendMessage(channelId: String, init: ResponseScope.() -> Unit): HttpResponse {
        val message = ResponseScope(this).apply(init).build()
        return createChannelMessage(channelId, message)
    }

    /**
     * Sends a response message to a Discord interaction. This method creates an interaction response
     * of type `CHANNEL_MESSAGE_WITH_SOURCE` and utilizes the passed `Message` configuration.
     *
     * @param interaction The interaction object representing the Discord interaction to respond to.
     * @param init A lambda function used to configure the message to be sent as a response.
     */
    suspend fun respondWithMessage(interaction: Interaction, init: (MessagePayload.() -> Unit)) {
        createInteractionResponse(
            interaction.id.value,
            interaction.token,
            InteractionCallbackTypes.CHANNEL_MESSAGE_WITH_SOURCE,
            MessagePayload().apply(init)
        )
    }
    //endregion

    //#region WebSocket

    /**
     * Log the bot in discord.
     * After log in, the bot will be able to receive events from discord.
     *
     * @param intents the discord intents identification number.
     *
     * @see [DiscordIntents](https://discord.com/developers/docs/events/gateway#gateway-intents)
     */
    fun login(intents: Int) = wssSession.connect(intents)

    /**
     * Shut the client down: cancel every coroutine it owns (Gateway loop, heartbeat) and release
     * the underlying HTTP client. The instance must not be reused afterwards.
     */
    fun close() {
        scope.cancel()
        httpClient.close()
    }

    //#endregion

    //#region Interaction routing

    /**
     * Register a [handler] for an interaction, keyed by [id]. The [kind] selects both the registry
     * and — via a typed key — the handler's receiver scope:
     *
     * - [InteractionKind.Command] : [id] is an application command **name**; receiver is
     *   [CommandInteractionScope] (`respond` / `defer` / `editOriginal`).
     * - [InteractionKind.Component] : [id] is a component **`custom_id`**; receiver is
     *   [ComponentInteractionScope] (`respond` / `update` / `defer` / `editOriginal`).
     *
     * Registering the same (kind, id) again replaces the previous handler. This is how you react to
     * a component **without** a surrounding command handler (e.g. a persistent button with a stable
     * `custom_id`).
     *
     * ```
     * client.on(InteractionKind.Command, "ping") { respond { content = "pong" } }
     * client.on(InteractionKind.Component, "menu-refresh") { update { content = "refreshed" } }
     * ```
     */
    fun <S : InteractionScope> on(kind: InteractionKind<S>, id: String, handler: suspend S.() -> Unit) {
        kind.register(this, id, handler)
    }

    /**
     * Shorthand for [on] with [InteractionKind.Command]: register the [handler] run when the
     * application command named [commandName] is invoked.
     *
     * ```
     * client.on("ping") {
     *     respond {
     *         content = "pong"
     *         button("again") { style = ButtonStyle.PRIMARY }.click { respond { content = "pong" } }
     *     }
     * }
     * ```
     */
    fun on(commandName: String, handler: CommandHandler) = on(InteractionKind.Command, commandName, handler)

    /**
     * Register a [handler] run for every Gateway event of type [T] (e.g. `MessageCreateEvent`).
     * Several handlers may be registered for the same type; they run concurrently, each in its own
     * coroutine. The handler receives an [EventScope] exposing the incoming `event` and, for events
     * that support it, reply verbs (see `EventScope.reply`).
     *
     * Register handlers **before** [login]: the dispatch loop starts at construction, so no early
     * event is missed once the Gateway connects.
     *
     * ```
     * client.on<ReadyEvent> { println("connected") }
     * client.on<MessageCreateEvent> { if (event.message.content == "ping") reply { content = "pong" } }
     * ```
     */
    inline fun <reified T : DispatchEvent> on(noinline handler: suspend EventScope<T>.() -> Unit) {
        @Suppress("UNCHECKED_CAST")
        registerEventHandler(T::class, handler as suspend EventScope<DispatchEvent>.() -> Unit)
    }

    // Called by the reified `on<T>()` to store an event handler. The cast in `on` is safe because a
    // handler is only ever invoked with an event of the class it was registered under (see the
    // `events.collect` dispatch loop). `@PublishedApi internal` so the public inline `on` can reach it.
    @PublishedApi
    internal fun registerEventHandler(
        type: KClass<out DispatchEvent>,
        handler: suspend EventScope<DispatchEvent>.() -> Unit,
    ) {
        eventHandlers.getOrPut(type) { mutableListOf() }.add(handler)
    }

    // Called by InteractionKind.Command.register to bind a command handler to its name.
    internal fun registerCommandHandler(commandName: String, handler: CommandHandler) {
        commandHandlers[commandName] = handler
    }

    // Called by ButtonHandle.click / InteractionKind.Component.register to bind a component callback.
    internal fun registerComponentHandler(customId: String, handler: ComponentHandler) {
        componentHandlers[customId] = handler
    }

    private suspend fun dispatchInteraction(interaction: Interaction) {
        when (val data = interaction.data) {
            is ApplicationCommandData -> {
                val handler = commandHandlers[data.name]
                if (handler != null) handler(CommandInteractionScope(interaction, this))
                else interactionLogger.debug { "No handler registered for command '${data.name}'" }
            }

            is MessageComponentData -> {
                val handler = componentHandlers[data.customId]
                if (handler != null) handler(ComponentInteractionScope(interaction, this))
                else interactionLogger.debug { "No handler registered for component '${data.customId}'" }
            }

            else -> interactionLogger.debug {
                "Unhandled interaction (type=${interaction.type}, data=${data?.let { it::class.simpleName }})"
            }
        }
    }

    //#endregion
}
