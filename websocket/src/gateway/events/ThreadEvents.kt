package ktordiscord.gateway.events

import ktordiscord.components.Channel
import ktordiscord.components.Snowflake
import ktordiscord.components.ThreadMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `THREAD_CREATE`: a thread was created, or the bot was added to an existing thread. A thread is a
 * [Channel] whose [Channel.type] is one of the thread channel types.
 */
@Serializable
data class ThreadCreateEvent(
    override var sequenceId: Int,
    val thread: Channel,
) : DispatchEvent()

/**
 * `THREAD_UPDATE`: a thread was updated (renamed, archived, locked…).
 */
@Serializable
data class ThreadUpdateEvent(
    override var sequenceId: Int,
    val thread: Channel,
) : DispatchEvent()

/**
 * `THREAD_DELETE`: a thread was deleted. The payload is a partial [Channel] (id, guild_id,
 * parent_id, type).
 */
@Serializable
data class ThreadDeleteEvent(
    override var sequenceId: Int,
    val thread: Channel,
) : DispatchEvent()

/**
 * `THREAD_LIST_SYNC`: sent when the bot gains access to a channel, listing its active threads.
 */
@Serializable
data class ThreadListSyncEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("channel_ids") val channelIds: List<Snowflake>? = null,
    val threads: List<Channel> = emptyList(),
    val members: List<ThreadMember> = emptyList(),
) : DispatchEvent()

/**
 * `THREAD_MEMBER_UPDATE`: the current user's thread member object changed. The payload is a thread
 * member object with an extra `guild_id` field (decoded separately into [guildId]).
 */
data class ThreadMemberUpdateEvent(
    override var sequenceId: Int,
    val guildId: Snowflake?,
    val member: ThreadMember,
) : DispatchEvent()

/**
 * `THREAD_MEMBERS_UPDATE`: users were added to or removed from a thread.
 */
@Serializable
data class ThreadMembersUpdateEvent(
    override var sequenceId: Int = 0,
    val id: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("member_count") val memberCount: Int = 0,
    @SerialName("added_members") val addedMembers: List<ThreadMember>? = null,
    @SerialName("removed_member_ids") val removedMemberIds: List<Snowflake>? = null,
) : DispatchEvent()
