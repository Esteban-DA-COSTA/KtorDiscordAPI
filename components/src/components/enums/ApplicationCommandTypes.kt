package components.enums

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(ApplicationCommandTypes.ApplicationCommandTypesSerializer::class)
enum class ApplicationCommandTypes(val id: Int) {
    CHAT_INPUT(1),
    USER(2),
    MESSAGE(3),
    PRIMARY_ENTRY_POINT(4);
    
    companion object ApplicationCommandTypesSerializer: KSerializer<ApplicationCommandTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("ApplicationCommandDataTypes", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder) = entries[decoder.decodeInt() - 1]
        override fun serialize(encoder: Encoder, value: ApplicationCommandTypes) = encoder.encodeInt(value.id)
    }
}