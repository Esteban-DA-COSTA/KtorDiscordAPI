package ktordiscord.gateway.events

import ktordiscord.components.Member
import ktordiscord.components.Message
import ktordiscord.components.Snowflake
import ktordiscord.components.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageCreateEvent(
    override var sequenceId: Int,
    val message: Message,
    @SerialName("guild_id")
    val guildId: Snowflake?,
    val member: Member?,
    val mentions: List<User>?
) : DispatchEvent()

@Serializable
data class MessageUpdateEvent(
    override var sequenceId: Int,
    val message: Message,
    @SerialName("guild_id")
    val guildId: Snowflake?,
    val member: Member?,
    val mentions: List<User>?
) : DispatchEvent()

@Serializable
data class MessageDeleteEvent(
    override var sequenceId: Int = 0,
    val id: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake?,
) : DispatchEvent()

/**
 * `MESSAGE_DELETE_BULK`: multiple messages were deleted at once (e.g. bulk-delete).
 */
@Serializable
data class MessageDeleteBulkEvent(
    override var sequenceId: Int = 0,
    val ids: List<Snowflake> = emptyList(),
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake?,
) : DispatchEvent()
