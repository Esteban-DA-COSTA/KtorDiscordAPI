package components.enums

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(components.enums.InteractionContextTypes.Serializer::class)
enum class InteractionContextTypes(val id: Int) {
    GUILD(0),
    BOT_DM(1),
    PRIVATE_CHANNEL(2);

    companion object Serializer : KSerializer<components.enums.InteractionContextTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("interactionContextTypes", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder) = components.enums.InteractionContextTypes.entries[decoder.decodeInt()]

        override fun serialize(encoder: Encoder, value: components.enums.InteractionContextTypes) = encoder.encodeInt(value.id)
    }
}