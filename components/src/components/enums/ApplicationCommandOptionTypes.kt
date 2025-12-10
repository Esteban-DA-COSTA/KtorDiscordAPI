package components.enums

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Discord Application Command Option Types.
 * Note: Discord encodes these as integers in JSON. We mark it @Serializable so the project compiles.
 * If integer mapping is required later, a custom serializer can be added.
 */
@Serializable(ApplicationCommandOptionTypes.ApplicationCommandOptionTypesSerializer::class)
enum class ApplicationCommandOptionTypes(val id: Int) {
    SUB_COMMAND(1),
    SUB_COMMAND_GROUP(2),
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7),
    ROLE(8),
    MENTIONABLE(9),
    NUMBER(10),
    ATTACHMENT(11);

    companion object ApplicationCommandOptionTypesSerializer: KSerializer<ApplicationCommandOptionTypes> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor = buildSerialDescriptor("ApplicationCommandOptionTypes", PrimitiveKind.INT)
        override fun deserialize(decoder: Decoder): ApplicationCommandOptionTypes = entries[decoder.decodeInt() - 1]
        override fun serialize(encoder: Encoder, value: ApplicationCommandOptionTypes) = encoder.encodeInt(value.id)
    }
}
