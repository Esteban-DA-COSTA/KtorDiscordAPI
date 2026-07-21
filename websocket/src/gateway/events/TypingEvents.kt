package ktordiscord.gateway.events

import ktordiscord.components.Member
import ktordiscord.components.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `TYPING_START`: a user started typing in a channel. [member] is present only in a guild.
 * [timestamp] is a Unix time (seconds).
 */
@Serializable
data class TypingStartEvent(
    override var sequenceId: Int = 0,
    @SerialName("channel_id") val channelId: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    @SerialName("user_id") val userId: Snowflake,
    val timestamp: Long = 0,
    val member: Member? = null,
) : DispatchEvent()
