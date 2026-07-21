package ktordiscord.components

import ktordiscord.components.enums.ChannelTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: Snowflake,
    val type: ChannelTypes,
    @SerialName("guild_id")
    val guildId: Snowflake? = null,
    val position: Int? = null,
    val name: String? = null,
    val topic: String? = null,
    val nsfw: Boolean = false,
    @SerialName("last_message_id")
    val lastMessageId: Snowflake? = null,
    val bitrate: Int? = null,
    @SerialName("user_limit")
    val userLimit: Int? = null,
    val recipients: List<User>? = null,
)

/**
 * Payload used to **modify** an existing channel (PATCH). Every field is optional: only the
 * properties set here are sent, the rest are omitted (not sent as null).
 */
@Serializable
data class ModifyChannelPayload(
    var name: String? = null,
    var type: ChannelTypes? = null,
    var position: Int? = null,
    var topic: String? = null,
    var nsfw: Boolean? = null,
    @SerialName("rate_limit_per_user") var rateLimitPerUser: Int? = null,
    var bitrate: Int? = null,
    @SerialName("user_limit") var userLimit: Int? = null,
    @SerialName("parent_id") var parentId: String? = null,
    @SerialName("permission_overwrites") var permissionOverwrites: List<Overwrite>? = null,
)

/**
 * Payload used to **create** a channel inside a guild (POST). [name] is required; the rest is
 * optional and omitted when unset.
 */
@Serializable
data class CreateChannelPayload(
    var name: String = "new-channel",
    var type: ChannelTypes? = null,
    var topic: String? = null,
    var bitrate: Int? = null,
    @SerialName("user_limit") var userLimit: Int? = null,
    @SerialName("rate_limit_per_user") var rateLimitPerUser: Int? = null,
    var position: Int? = null,
    @SerialName("parent_id") var parentId: String? = null,
    var nsfw: Boolean? = null,
    @SerialName("permission_overwrites") var permissionOverwrites: List<Overwrite>? = null,
)

/**
 * Payload used to **bulk delete** messages in a channel (POST). Between 2 and 100 message ids,
 * none older than two weeks (Discord constraint).
 */
@Serializable
data class BulkDeleteMessagesPayload(
    var messages: List<String> = emptyList(),
)
