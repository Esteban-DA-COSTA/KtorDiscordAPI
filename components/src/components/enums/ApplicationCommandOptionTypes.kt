package components.enums

import kotlinx.serialization.Serializable

/**
 * Discord Application Command Option Types.
 * Note: Discord encodes these as integers in JSON. We mark it @Serializable so the project compiles.
 * If integer mapping is required later, a custom serializer can be added.
 */
@Serializable
enum class ApplicationCommandOptionTypes {
    SUB_COMMAND,
    SUB_COMMAND_GROUP,
    STRING,
    INTEGER,
    BOOLEAN,
    USER,
    CHANNEL,
    ROLE,
    MENTIONABLE,
    NUMBER,
    ATTACHMENT,
}
