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
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: Int? = null,
    @SerialName("owner_id")
    val ownerId: Snowflake? = null,
    @SerialName("parent_id")
    val parentId: Snowflake? = null,
    @SerialName("message_count")
    val messageCount: Int? = null,
    @SerialName("member_count")
    val memberCount: Int? = null,
    @SerialName("thread_metadata")
    val threadMetadata: ThreadMetadata? = null,
    val member: ThreadMember? = null,
)

/**
 * Thread-specific fields carried by a [Channel] that is a thread (embedded in the `thread_metadata`
 * object of the channel payload).
 */
@Serializable
data class ThreadMetadata(
    val archived: Boolean = false,
    @SerialName("auto_archive_duration")
    val autoArchiveDuration: Int? = null,
    @SerialName("archive_timestamp")
    val archiveTimestamp: String? = null,
    val locked: Boolean = false,
    val invitable: Boolean? = null,
    @SerialName("create_timestamp")
    val createTimestamp: String? = null,
)

/**
 * Represents a member of a thread (the current user's per-thread state, or an entry in
 * `THREAD_MEMBERS_UPDATE` / `THREAD_LIST_SYNC`).
 *
 * [id] and [userId] are omitted when the object describes the current user within a channel payload.
 */
@Serializable
data class ThreadMember(
    val id: Snowflake? = null,
    @SerialName("user_id")
    val userId: Snowflake? = null,
    @SerialName("join_timestamp")
    val joinTimestamp: String? = null,
    val flags: Int = 0,
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
