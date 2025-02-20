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

@Serializable(components.enums.InteractionTypes.Serializer::class)
enum class InteractionTypes(val id: Int) {
    PING(1),
    APPLICATION_COMMAND(2),
    MESSAGE_COMPONENT(3),
    APPLICATION_COMMAND_AUTOCOMPLETE(4),
    MODAL_SUBMIT(5);

    companion object Serializer : KSerializer<components.enums.InteractionTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("InteractionTypes", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder) = entries[decoder.decodeInt() - 1]
        override fun serialize(encoder: Encoder, value: components.enums.InteractionTypes) = encoder.encodeInt(value.id)
    }
}