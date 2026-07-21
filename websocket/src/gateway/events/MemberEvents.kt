package ktordiscord.gateway.events

import ktordiscord.components.Member
import ktordiscord.components.Snowflake
import ktordiscord.components.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `GUILD_MEMBER_ADD`: a user joined a guild. The payload is a guild member object with an extra
 * `guild_id` field (decoded separately into [guildId]).
 */
data class GuildMemberAddEvent(
    override var sequenceId: Int,
    val guildId: Snowflake?,
    val member: Member,
) : DispatchEvent()

/**
 * `GUILD_MEMBER_UPDATE`: a guild member was updated. Unlike [GuildMemberAddEvent] the payload has a
 * flat shape (no wrapping member object), so it maps directly onto this class.
 */
@Serializable
data class GuildMemberUpdateEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    val roles: List<Snowflake> = emptyList(),
    val user: User,
    val nick: String? = null,
    val avatar: String? = null,
    @SerialName("joined_at") val joinedAt: String? = null,
    @SerialName("premium_since") val premiumSince: String? = null,
    val deaf: Boolean? = null,
    val mute: Boolean? = null,
    val pending: Boolean? = null,
    @SerialName("communication_disabled_until") val communicationDisabledUntil: String? = null,
) : DispatchEvent()

/**
 * `GUILD_MEMBER_REMOVE`: a user was removed from a guild (left, kicked or banned).
 */
@Serializable
data class GuildMemberRemoveEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    val user: User,
) : DispatchEvent()

/**
 * `GUILD_MEMBERS_CHUNK`: response to a Request Guild Members (OP 8) command, streamed in chunks.
 */
@Serializable
data class GuildMembersChunkEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    val members: List<Member> = emptyList(),
    @SerialName("chunk_index") val chunkIndex: Int = 0,
    @SerialName("chunk_count") val chunkCount: Int = 0,
    @SerialName("not_found") val notFound: List<Snowflake>? = null,
    val nonce: String? = null,
) : DispatchEvent()
