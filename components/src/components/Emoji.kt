package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val id: Snowflake? = null,
    val name: String? = null,
    val roles: List<Role>? = null,
    val user: User? = null,
    @SerialName("require_colons") val requireColons: Boolean? = null,
    val managed: Boolean? = null,
    val animated: Boolean? = null,
    val available: Boolean? = null
)

/**
 * Payload used to **create** a guild emoji (POST).
 *
 * @property name the emoji name.
 * @property image the 128x128 emoji image as a base64 data URI (e.g. `data:image/png;base64,...`).
 * @property roles ids of the roles allowed to use this emoji, if restricted.
 */
@Serializable
data class CreateEmojiPayload(
    var name: String = "new_emoji",
    var image: String = "",
    var roles: List<Snowflake>? = null,
)

/**
 * Payload used to **modify** a guild emoji (PATCH). Both fields are optional.
 */
@Serializable
data class ModifyEmojiPayload(
    var name: String? = null,
    var roles: List<Snowflake>? = null,
)
