package gateway.events

import components.Presence
import gateway.OPCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(IdentifySerializer::class)
class IdentifyEvent(
    token: String,
    intents: Int
) : Event() {

    @SerialName("d")
    val data = IdentifyData(token, intents)
}

@Serializable
data class IdentifyData(
    val token: String,
    val intents: Int,
    val properties: Properties = Properties(),
    val presence: Presence = Presence()
) {
    @Serializable
    data class Properties(
        val os: String = "Windows",
        val browser: String = "Edge",
        val device: String = "PC"
    )
}

object IdentifySerializer : KSerializer<IdentifyEvent> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("identify") {
            element("op", OPCode.serializer().descriptor)
            element("d", IdentifyData.serializer().descriptor)
        }

    override fun serialize(encoder: Encoder, value: IdentifyEvent) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, OPCode.serializer(), OPCode.IDENTIFY)
        encodeSerializableElement(descriptor, 1, IdentifyData.serializer(), value.data)
    }

    override fun deserialize(decoder: Decoder): IdentifyEvent {
        TODO("Not yet implemented")
    }
}