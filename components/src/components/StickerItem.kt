package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The smallest amount of data required to render a sticker, as carried by [Message.stickerItems].
 * A partial of the full [Sticker] object.
 *
 * @property id the sticker id.
 * @property name the sticker name.
 * @property formatType the [sticker format type](https://discord.com/developers/docs/resources/sticker#sticker-format-types).
 *
 * @see [Sticker Item Object](https://discord.com/developers/docs/resources/sticker#sticker-item-object)
 */
@Serializable
data class StickerItem(
    val id: Snowflake,
    val name: String,
    @SerialName("format_type") val formatType: Int,
)
