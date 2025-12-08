import components.Message
import components.Snowflake
import components.enums.InteractionTypes
import components.interactions.ApplicationCommand
import components.interactions.Interaction
import gateway.events.DispatchEvent
import interactions.ApplicationCommandAction
import interactions.InteractionManager
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.collections.set

/**
 * Discord client.
 *
 * @property token The application identification token.
 */
class DiscordClient(internal val token: String) {
    internal val httpClientLogger = KotlinLogging.logger("HTTP_LOGGER")
    private val wsClientLogger = KotlinLogging.logger("WS_LOGGER")

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

    // Channel for classic events received from Discord
    val events = Channel<DispatchEvent>()

    // Channel for interaction events
    val interactions = Channel<Interaction>()

    // the web socket session used to receive/send Gateway intents
    private val wssSession = DiscordWebSocketSession(httpClient, events, interactions, token)

    internal var apiVersion = 10
    internal var discordURL = "https://discord.com/api/v$apiVersion"

    internal lateinit var applicationId: String
    private lateinit var interactionManager: InteractionManager

    init {
        runBlocking {
            launch {
                applicationId = getMeApplicationId().value
                interactionManager = InteractionManager(this@DiscordClient)
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            for (interaction in interactions) {
                when (interaction.type) {
                    InteractionTypes.APPLICATION_COMMAND -> {
                        onInteractionReceived(interaction)
                    }

                    else -> {
                        this@DiscordClient.wsClientLogger.error {
                            "Received an interaction that I don't know how to handle: ${interaction.type}"
                        }
                    }
                }
            }
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
    suspend fun sendMessage(channelId: String, init: (Message.() -> Unit)): HttpResponse {
        val message = Message().apply(init)
        return createChannelMessage(channelId, message)

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

    //#endregion

    /**
     * Define the callback function to use when receiving a specific command.
     * @param commandName The name of the command.
     * @param action the action function that will be run when receiving the command.
     */
    fun on(commandName: String, action: suspend ApplicationCommandAction.(Interaction) -> Unit) {
        try {
            val command = interactionManager.appCommands.keys.firstOrNull { it.equals(commandName) }
                ?: throw NoSuchElementException()
            assignCommandAction(command, action)
        } catch (_: NoSuchElementException) {
            interactionManager.logger.error { "Interaction command $commandName not found" }

        }
    }

    fun on(commandId: Snowflake, action: suspend ApplicationCommandAction.(Interaction) -> Unit) {
        try {
            val command = interactionManager.appCommands.keys.firstOrNull { it.equals(commandId) }
                ?: throw NoSuchElementException()
            assignCommandAction(command, action)
        } catch (_: NoSuchElementException) {
            interactionManager.logger.error { "Interaction command $commandId not found" }
        }
    }

    private fun assignCommandAction(command: ApplicationCommand, action: suspend ApplicationCommandAction.(Interaction) -> Unit) {
        val commandAction = ApplicationCommandAction(command, this, action)
        interactionManager.appCommands[command] = commandAction
    }


    private suspend fun onInteractionReceived(interaction: Interaction) {
        interactionManager.appCommands.keys.firstOrNull(interaction.data!!::equals)?.let {
            interactionManager.appCommands[it]?.executeAction(interaction)
        }

    }
}
