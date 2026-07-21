package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

/**
 * Discord message component types. Encoded as integers in JSON.
 *
 * Covers the interactive components a bot commonly receives or sends (action rows, buttons and the
 * select-menu family). Unknown values (e.g. newer "Components V2" types) decode to [UNKNOWN] rather
 * than failing, so a component received from Discord never breaks decoding.
 *
 * @see [Component Object](https://discord.com/developers/docs/interactions/message-components#component-object-component-types)
 */
@Serializable(ComponentType.Serializer::class)
enum class ComponentType(val id: Int) {
    ACTION_ROW(1),
    BUTTON(2),
    STRING_SELECT(3),
    TEXT_INPUT(4),
    USER_SELECT(5),
    ROLE_SELECT(6),
    MENTIONABLE_SELECT(7),
    CHANNEL_SELECT(8),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ComponentType>(
        "ComponentType", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
