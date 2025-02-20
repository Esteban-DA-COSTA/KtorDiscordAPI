package components.enums

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(StatusTypeEnum.Serializer::class)
enum class StatusTypeEnum(val text: String) {
    ONLINE("online"),
    DO_NOT_DISTURB("dnd"),
    AFK("idle"),
    INVISIBLE("invisible"),
    OFFLINE("offline");

    companion object Serializer : KSerializer<StatusTypeEnum> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("status", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) = getByText(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: StatusTypeEnum) = encoder.encodeString(value.text)

        fun getByText(text: String) = values().first { it.text == text }
    }
}
