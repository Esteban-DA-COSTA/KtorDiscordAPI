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

@Serializable(components.enums.EmbedTypes.Serializer::class)
enum class EmbedTypes {
    RICH,
    IMAGE,
    VIDEO,
    GIFV,
    ARTICLE,
    LINK;

    companion object Serializer : KSerializer<components.enums.EmbedTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = buildSerialDescriptor("type", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) = components.enums.EmbedTypes.valueOf(decoder.decodeString().uppercase())
        override fun serialize(encoder: Encoder, value: components.enums.EmbedTypes) = encoder.encodeString(value.name.lowercase())
    }
}