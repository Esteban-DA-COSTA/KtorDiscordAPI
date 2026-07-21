package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val user: User? = null,
    val nick: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val roles: List<Snowflake>? = null,
    @SerialName("joined_at")
    val joinedAt: String? = null,
    @SerialName("premium_since")
    val premiumSince: String? = null,
    val deaf: Boolean = false,
    val mute: Boolean = false,
    val pending: Boolean = false,
    val flags: Int? = null,
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: String? = null,
    val permissions: String? = null
)

/**
 * Payload used to **add** a member to a guild (PUT `/guilds/{id}/members/{uid}`).
 *
 * Requires an OAuth2 [accessToken] for the target user, granted the `guilds.join` scope — this is
 * *not* the bot token. The remaining fields are optional overrides applied on join.
 */
@Serializable
data class AddMemberPayload(
    @SerialName("access_token") var accessToken: String,
    var nick: String? = null,
    var roles: List<Snowflake>? = null,
    var mute: Boolean? = null,
    var deaf: Boolean? = null,
)

/**
 * Payload used to **modify** a guild member (PATCH). Every field is optional; only the properties
 * set here are sent.
 */
@Serializable
data class ModifyMemberPayload(
    var nick: String? = null,
    var roles: List<Snowflake>? = null,
    var mute: Boolean? = null,
    var deaf: Boolean? = null,
    @SerialName("channel_id") var channelId: Snowflake? = null,
    @SerialName("communication_disabled_until") var communicationDisabledUntil: String? = null,
)
