package components.enums

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(InteractionCallbackTypes.InteractionsCallbackTypesSerializer::class)
enum class InteractionCallbackTypes(val id: Int) {
    PONG(1),
    CHANNEL_MESSAGE_WITH_SOURCE(4),
    DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),
    DEFERRED_UPDATE_MESSAGE(6),
    UPDATE_MESSAGE(7),
    APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8),
    MODAL(9);
    
    companion object InteractionsCallbackTypesSerializer: KSerializer<InteractionCallbackTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor = buildSerialDescriptor("InteractionCallbackTypes", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InteractionCallbackTypes =
            InteractionCallbackTypes.entries.first { it.id == decoder.decodeInt() }

        override fun serialize(encoder: Encoder, value: InteractionCallbackTypes) = encoder.encodeInt(value.id)
    }
}