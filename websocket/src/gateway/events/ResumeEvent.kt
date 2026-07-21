package ktordiscord.gateway.events

import ktordiscord.gateway.OPCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(ResumeSerializer::class)
class ResumeEvent(
    token: String,
    sessionId: String,
    seq: Int
) : Event() {

    @SerialName("d")
    val data = ResumeData(token, sessionId, seq)
}

@Serializable
data class ResumeData(
    val token: String,
    @SerialName("session_id") val sessionId: String,
    val seq: Int
)

object ResumeSerializer : OutgoingEventSerializer<ResumeEvent, ResumeData>(
    "resume", OPCode.RESUME, ResumeData.serializer(), { it.data }
)
