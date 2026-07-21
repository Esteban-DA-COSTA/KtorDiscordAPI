package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: Long,
    val name: String,
    val description: String?,
    val color: Int,
    val hoist: Boolean,
    val icon: String?,
    @SerialName("unicode_emoji") val unicodeEmoji: String?,
    val position: Int,
    val permissions: String,
    val managed: Boolean,
    val mentionable: Boolean,
    val tags: RoleTag? = null,
    val flags: Int = 0
)

@Serializable
data class RoleTag(
    @SerialName("bot_id") val botId: Long?,
    @SerialName("integration_id") val integrationId: Long? = null,
    @SerialName("subscription_listing_id") val subscriptionListingId: Long? = null
)

@Serializable
data class RolePayload(
    var name: String = "new role",
    var permissions: String? = null,
    var color: Int = 0,
    var hoist: Boolean = false,
    var icon: List<Byte>? = null,
    @SerialName("unicode_emoji") var unicodeEmoji: String? = null,
    var mentionable: Boolean = false
)

/**
 * Single entry of the **modify role positions** payload (PATCH `/guilds/{id}/roles`): a role id and
 * its new [position]. A `null` position leaves the role's position unchanged.
 */
@Serializable
data class RolePositionPayload(
    var id: String,
    var position: Int? = null,
)
