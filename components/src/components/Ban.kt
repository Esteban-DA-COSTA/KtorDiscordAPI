package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A guild ban entry (see Discord "Ban Object"), as returned by the get bans endpoints.
 *
 * @property reason the reason for the ban, if any.
 * @property user the banned user.
 */
@Serializable
data class Ban(
    val reason: String? = null,
    val user: User,
)

/**
 * Payload used to **create** a guild ban.
 *
 * @property deleteMessageSeconds number of seconds of the banned user's message history to delete
 * (0–604800, i.e. up to 7 days). Left `null` (omitted) to keep the messages.
 */
@Serializable
data class CreateBanPayload(
    @SerialName("delete_message_seconds")
    var deleteMessageSeconds: Int? = null,
)
