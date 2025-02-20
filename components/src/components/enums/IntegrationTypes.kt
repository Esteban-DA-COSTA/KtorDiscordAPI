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

@Serializable(components.enums.IntegrationTypes.Serializer::class)
enum class IntegrationTypes(val id: Int) {
    GUILD_INSTALL(0),
    USER_INSTALL(1);

    companion object Serializer : KSerializer<components.enums.IntegrationTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("integrationType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) = components.enums.IntegrationTypes.entries[decoder.decodeInt()]

        override fun serialize(encoder: Encoder, value: components.enums.IntegrationTypes) = encoder.encodeInt(value.id)
    }
}