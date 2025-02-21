import components.Message
import components.enums.InteractionCallbackTypes
import components.interactions.Interaction
import gateway.events.DispatchEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Discord client.
 *
 * @property token The application identification token.
 */
class DiscordClient(internal val token: String) {
    private val httpClientLogger = KotlinLogging.logger("HTTP_LOGGER")

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
    
    suspend fun respondWithMessage(interaction: Interaction, init: (Message.() -> Unit)) {
        createInteractionResponse(interaction.id.value,interaction.token, InteractionCallbackTypes.CHANNEL_MESSAGE_WITH_SOURCE, Message().apply(init))
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
}
