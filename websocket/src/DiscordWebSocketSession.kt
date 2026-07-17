package ktordiscord.gateway

import ktordiscord.components.interactions.Interaction
import ktordiscord.gateway.events.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.random.Random

class DiscordWebSocketSession(
    private val httpClient: HttpClient,
    private val scope: CoroutineScope,
    private val channelEvents: MutableSharedFlow<DispatchEvent>,
    private val channelInteraction: MutableSharedFlow<Interaction>,
    private val token: String
) {
    private val wssLogger = KotlinLogging.logger("WSS_LOGGER")

    private lateinit var wssSession: DefaultClientWebSocketSession

    @Volatile
    private var hasReceiveHBACK = true

    @Volatile
    private var lastSequenceNumber: Int? = null

    private var heartBeatInterval = 0
    private var heartbeatJob: Job? = null

    // Resume state
    private var sessionId: String? = null
    private var resumeGatewayUrl: String? = null
    private var intents: Int = 0

    companion object {
        private val FATAL_CLOSE_CODES = setOf(4004, 4010, 4011, 4012, 4013, 4014)
        private val RE_IDENTIFY_CLOSE_CODES = setOf(4009)
    }

    /**
     * Connect discord bot to websockets session.
     * Manages reconnection automatically on disconnects.
     *
     * @param intents the discord intents identification number.
     */
    fun connect(intents: Int) = scope.launch {
        this@DiscordWebSocketSession.intents = intents
        var resuming = false

        while (true) {
            try {
                val host = if (resuming && resumeGatewayUrl != null)
                    extractHost(resumeGatewayUrl!!)
                else
                    "gateway.discord.gg"

                wssLogger.info { "Connecting to $host (resume=$resuming)" }

                wssSession = httpClient.webSocketSession(
                    method = HttpMethod.Get,
                    host = host,
                    path = "/?v=10&encoding=json"
                )

                handleSession(resuming)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                wssLogger.error { "WebSocket error: ${e.message}" }
            }

            heartbeatJob?.cancel()

            val closeCode = try {
                wssSession.closeReason.await()?.code?.toInt() ?: 1006
            } catch (_: Exception) {
                1006
            }

            wssLogger.info { "Connection closed with code $closeCode" }

            if (closeCode in FATAL_CLOSE_CODES) {
                wssLogger.error { "Fatal close code $closeCode, not reconnecting" }
                break
            }

            if (closeCode in RE_IDENTIFY_CLOSE_CODES || sessionId == null) {
                resuming = false
                sessionId = null
                resumeGatewayUrl = null
                lastSequenceNumber = null
            } else {
                resuming = true
            }

            val backoff = Random.nextLong(1000, 5000)
            wssLogger.info { "Reconnecting in ${backoff}ms (resume=$resuming)" }
            delay(backoff)
        }
    }

    /**
     * Handle a single WebSocket session: read frames and dispatch events.
     */
    private suspend fun handleSession(resuming: Boolean) {
        for (frame in wssSession.incoming) {
            when (frame) {
                is Frame.Text -> {
                    when (val event = wssSession.converter?.deserialize<Event>(frame)) {
                        is HelloEvent -> onHello(event, resuming)
                        is HBackEvent -> hasReceiveHBACK = true
                        is DispatchEvent -> onReceiveDispatchEvent(event)
                        is ReconnectEvent -> onReconnect()
                        is InvalidSessionEvent -> onInvalidSession(event)
                        is HeartbeatEvent -> onServerHeartbeatRequest()
                        else -> wssLogger.warn { "Unhandled event: ${event!!::class.simpleName}" }
                    }
                }

                else -> wssLogger.error { "Frame type not recognized" }
            }
        }
    }

    /**
     * Handle Hello event: start heartbeat and identify or resume.
     */
    private suspend fun onHello(event: HelloEvent, resuming: Boolean) {
        heartBeatInterval = event.heartbeatInterval
        hasReceiveHBACK = true

        // Jittered first heartbeat
        delay((heartBeatInterval * Random.nextFloat()).toLong())
        wssSession.sendSerialized(HeartbeatEvent(lastSequenceNumber?.toLong() ?: 0))
        hasReceiveHBACK = false

        if (resuming && sessionId != null) {
            sendResume()
        } else {
            initiateIdentification()
        }

        heartbeatJob?.cancel()
        heartbeatJob = scope.launch { heartbeatLoop() }
    }

    /**
     * Handle incoming dispatch events: track sequence, capture session state, forward to channels.
     */
    private suspend fun onReceiveDispatchEvent(event: DispatchEvent) {
        lastSequenceNumber = event.sequenceId

        if (event is ReadyEvent) {
            sessionId = event.sessionId
            resumeGatewayUrl = event.resumeGatewayUrl
            wssLogger.info { "Session ready: id=$sessionId, resumeUrl=$resumeGatewayUrl" }
        }

        if (event is InteractionCreateEvent) {
            channelInteraction.emit(event.interaction)
        } else {
            channelEvents.emit(event)
        }
    }

    /**
     * Server requested reconnect (OP 7): close with resumable code.
     */
    private suspend fun onReconnect() {
        wssLogger.info { "Received RECONNECT from server" }
        wssSession.close(CloseReason(4000.toShort(), "Server requested reconnect"))
    }

    /**
     * Invalid session (OP 9): reset state if not resumable, then close.
     */
    private suspend fun onInvalidSession(event: InvalidSessionEvent) {
        wssLogger.warn { "Invalid session, resumable=${event.resumable}" }
        if (!event.resumable) {
            sessionId = null
            resumeGatewayUrl = null
            lastSequenceNumber = null
        }
        wssSession.close(CloseReason(4000.toShort(), "Invalid session"))
    }

    /**
     * Server requested an immediate heartbeat (OP 1 received).
     */
    private suspend fun onServerHeartbeatRequest() {
        wssLogger.info { "Server requested immediate heartbeat" }
        wssSession.sendSerialized(HeartbeatEvent(lastSequenceNumber?.toLong() ?: 0))
    }

    /**
     * Send heartbeats at regular intervals.
     * Closes the connection if no ACK was received since the last heartbeat.
     */
    private suspend fun heartbeatLoop() {
        while (true) {
            delay(heartBeatInterval.toLong())
            if (!hasReceiveHBACK) {
                wssLogger.warn { "No heartbeat ACK received, closing for resume" }
                wssSession.close(CloseReason(4000.toShort(), "Zombie connection"))
                return
            }
            hasReceiveHBACK = false
            wssSession.sendSerialized(HeartbeatEvent(lastSequenceNumber?.toLong() ?: 0))
        }
    }

    /**
     * Send Resume (OP 6) to restore a previous session.
     */
    private suspend fun sendResume() {
        wssLogger.info { "Sending RESUME for session $sessionId, seq=$lastSequenceNumber" }
        val resume = ResumeEvent(token, sessionId!!, lastSequenceNumber ?: 0)
        val frame = wssSession.converter?.serialize(resume) as Frame.Text
        wssSession.send(frame)
    }

    /**
     * Send Identify (OP 2) to start a new session.
     */
    private suspend fun initiateIdentification() {
        val identify = IdentifyEvent(token, intents)
        val frame = wssSession.converter?.serialize(identify) as Frame.Text
        wssSession.send(frame)
    }

    /**
     * Extract host from a wss:// URL (e.g. "wss://gateway-us-east1-b.discord.gg").
     */
    private fun extractHost(url: String): String {
        return url.removePrefix("wss://").removePrefix("ws://").trimEnd('/')
    }
}
