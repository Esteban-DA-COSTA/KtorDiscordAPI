import components.interactions.Interaction
import gateway.events.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class DiscordWebSocketSession(
    private val httpClient: HttpClient,
    private val channelEvents: Channel<DispatchEvent>,
    private val channelInteraction: Channel<Interaction>,
    private val token: String
) {
    private val wssLogger = KotlinLogging.logger("WSS_LOGGER")

    private lateinit var wssSession: DefaultClientWebSocketSession

    private var hasReceiveHBACK = false

    // The heartbeat interval to ping the discord websocket
    private var heartBeatInterval = 0

    // The last sequence
    private var lastSequenceNumber: Int? = null

    // Used to avoid infinite loop
    private var needGatewayClose = false

    suspend fun connect(intents: Int) = CoroutineScope(Dispatchers.Default).launch {
        wssSession = httpClient.webSocketSession(
            method = HttpMethod.Get,
            host = "gateway.discord.gg",
            path = "/?v=10&encoding=json"
        )
        for (frame in wssSession.incoming) {
            when (frame) {
                is Frame.Text -> {
                    when (val event = wssSession.converter?.deserialize<Event>(frame)) {
                        is HelloEvent -> initHeartBeat(event, intents)
                        is HBackEvent -> hasReceiveHBACK = true
                        is DispatchEvent -> onReceiveDispatchEvent(event)
                        else -> println(event!!::class.simpleName)
                    }
                }

                else -> wssLogger.error { "Frame type not recognized" }
            }
        }
        wssSession.close()
    }


    private suspend fun onReceiveDispatchEvent(event: DispatchEvent) {
        if (event is InteractionCreateEvent) {
            channelInteraction.send(event.interaction)
        } else
            channelEvents.send(event)
    }

    /**
     * Initialize the  heartbeat for discord
     *
     * @param event the data of the heartbeat system
     */
    private suspend fun initHeartBeat(event: HelloEvent, intents: Int) {
        heartBeatInterval = event.heartbeatInterval
        // Use a random on the first heartbeat to avoid overheat
        delay((heartBeatInterval * Random.nextFloat()).toLong())
        wssSession.sendSerialized(HeartbeatEvent(lastSequenceNumber?.toLong() ?: 0))

        // Init Identify
        initiateIdentification(intents)
        // Launch a new coroutine to send heartbeat on every heartbeat interval
        sendHeartBeat()
    }


    /**
     * Sends a heartbeat to the server at regular intervals.
     *
     * If a heartbeat acknowledgment is not received, the WebSocket session is closed
     * with a 'NOT_CONSISTENT' close code and the reason 'No HeartBeat ACK received'.
     */
    private fun sendHeartBeat() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(heartBeatInterval.toLong())
                if (hasReceiveHBACK) {
                    println("heartbeat")
                    wssSession.sendSerialized(HeartbeatEvent(lastSequenceNumber?.toLong() ?: 0))
                } else {
                    wssSession.close(CloseReason(CloseReason.Codes.NOT_CONSISTENT, "No HeartBeat ACK received"))
                    hasReceiveHBACK = false
                    needGatewayClose = true
                }
            }
        }
    }

    /**
     * Initiates the identification process by building the IdentifyIntent.
     */
    private suspend fun initiateIdentification(intents: Int) {
        val identify = IdentifyEvent(token, intents)
        val frame = wssSession.converter?.serialize(identify) as Frame.Text
        wssLogger.debug { "Identify frame: ${frame.readText()}" }
        wssSession.send(frame)
    }
}