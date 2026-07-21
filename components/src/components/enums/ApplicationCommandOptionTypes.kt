package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

/**
 * Discord Application Command Option Types. Encoded as integers in JSON (via [IntEnumSerializer]),
 * with an [UNKNOWN] fallback for values Discord may add later.
 */
@Serializable(ApplicationCommandOptionTypes.Serializer::class)
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
    ATTACHMENT(11),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ApplicationCommandOptionTypes>(
        "ApplicationCommandOptionTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
