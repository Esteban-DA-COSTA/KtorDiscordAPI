package ktordiscord.gateway.events

import ktordiscord.gateway.OPCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

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

object ResumeSerializer : KSerializer<ResumeEvent> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("resume") {
            element("op", OPCode.serializer().descriptor)
            element("d", ResumeData.serializer().descriptor)
        }

    override fun serialize(encoder: Encoder, value: ResumeEvent) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, OPCode.serializer(), OPCode.RESUME)
        encodeSerializableElement(descriptor, 1, ResumeData.serializer(), value.data)
    }

    override fun deserialize(decoder: Decoder): ResumeEvent {
        TODO("Not yet implemented")
    }
}
