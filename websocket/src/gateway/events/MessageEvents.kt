package gateway.events

import components.Member
import components.Message
import components.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageCreateEvent(
    override var sequenceId: Int,
    val message: Message,
    @SerialName("guild_id")
    val guildId: String?,
    val member: Member?,
    val mentions: List<User>?
) : DispatchEvent()

data class MessageUpdateEvent(
    override var sequenceId: Int,
    val message: Message,
    @SerialName("guild_id")
    val guildId: String?,
    val member: Member?,
    val mentions: List<User>?
) : DispatchEvent()

@Serializable
data class MessageDeleteEvent(
    override var sequenceId: Int = 0,
    val id: String,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("guild_id")
    val guildId: String?,
) : DispatchEvent()
