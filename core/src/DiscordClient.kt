package ktordiscord.core

import ktordiscord.components.MessagePayload
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.enums.InteractionTypes
import ktordiscord.components.interactions.ApplicationCommand
import ktordiscord.components.interactions.Interaction
import ktordiscord.gateway.DiscordWebSocketSession
import ktordiscord.gateway.events.DispatchEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json

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

    internal var apiVersion = 10
    internal var discordURL = "https://discord.com/api/v$apiVersion"

    internal lateinit var applicationId: String
    private lateinit var interactionManager: InteractionManager

    companion object {
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
     * Send a message to a specific discord channel
     *
     * @param channelId the channel id to send a message
     * @param init the message builder function
     * @return the Http response from discord
     */
    suspend fun sendMessage(channelId: String, init: (MessagePayload.() -> Unit)): HttpResponse {
        val message = MessagePayload().apply(init)
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
}
