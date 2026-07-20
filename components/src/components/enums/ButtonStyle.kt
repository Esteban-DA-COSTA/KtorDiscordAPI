package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

/**
 * Discord message button styles. Encoded as integers in JSON.
 *
 * [LINK] buttons carry a `url` and no `custom_id`, so they cannot receive a click callback.
 *
 * @see [Button Object](https://discord.com/developers/docs/interactions/message-components#button-object-button-styles)
 */
@Serializable(ButtonStyle.Serializer::class)
enum class ButtonStyle(val id: Int) {
    PRIMARY(1),
    SECONDARY(2),
    SUCCESS(3),
    DANGER(4),
    LINK(5),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ButtonStyle>(
        "ButtonStyle", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
