import components.Message
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

    // The http client used to send request
    @OptIn(ExperimentalSerializationApi::class)
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

    val events = Channel<DispatchEvent>()
    val interactions = Channel<Interaction>()

    // the web socket session used to receive/send Gateway intents
    private val wssSession = DiscordWebSocketSession(httpClient, events, interactions, token)

    internal var apiVersion = 10
    internal var discordURL = "https://discord.com/api/v$apiVersion"

    //#region HTTP Calls

    //endregion

    suspend fun sendMessage(channelId: String, init: (Message.() -> Unit)): HttpResponse {
        val message = Message().apply(init)
        return createChannelMessage(channelId, message)

    }

    //#region WebSocket

    suspend fun login(intents: Int) = wssSession.connect(intents)

    //#endregion
}
