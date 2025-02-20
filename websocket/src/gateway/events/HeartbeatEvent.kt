package gateway.events

import gateway.OPCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable(with = HeartbeatSerializer::class)
data class HeartbeatEvent(
    val d: Long
) : Event()

object HeartbeatSerializer : KSerializer<HeartbeatEvent> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("heartbeat") {
            element("op", Int.serializer().descriptor)
            element("d", Long.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): HeartbeatEvent = decoder.decodeStructure(descriptor) {
        HeartbeatEvent(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: HeartbeatEvent) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, OPCode.serializer(), OPCode.HEARTBEAT)
        encodeLongElement(descriptor, 1, value.d)
    }
}