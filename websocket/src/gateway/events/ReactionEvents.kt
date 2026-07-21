package ktordiscord.gateway.events

import ktordiscord.components.Emoji
import ktordiscord.components.Member
import ktordiscord.components.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `MESSAGE_REACTION_ADD`: a user added a reaction to a message. [member] is present only for
 * reactions in a guild.
 */
@Serializable
data class MessageReactionAddEvent(
    override var sequenceId: Int = 0,
    @SerialName("user_id") val userId: Snowflake,
    @SerialName("channel_id") val channelId: Snowflake,
    @SerialName("message_id") val messageId: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    val member: Member? = null,
    val emoji: Emoji,
    @SerialName("message_author_id") val messageAuthorId: Snowflake? = null,
    val burst: Boolean = false,
) : DispatchEvent()

/**
 * `MESSAGE_REACTION_REMOVE`: a user removed a reaction from a message.
 */
@Serializable
data class MessageReactionRemoveEvent(
    override var sequenceId: Int = 0,
    @SerialName("user_id") val userId: Snowflake,
    @SerialName("channel_id") val channelId: Snowflake,
    @SerialName("message_id") val messageId: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    val emoji: Emoji,
    val burst: Boolean = false,
) : DispatchEvent()

/**
 * `MESSAGE_REACTION_REMOVE_ALL`: every reaction was removed from a message at once.
 */
@Serializable
data class MessageReactionRemoveAllEvent(
    override var sequenceId: Int = 0,
    @SerialName("channel_id") val channelId: Snowflake,
    @SerialName("message_id") val messageId: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake? = null,
) : DispatchEvent()

/**
 * `MESSAGE_REACTION_REMOVE_EMOJI`: every reaction for a single emoji was removed from a message.
 */
@Serializable
data class MessageReactionRemoveEmojiEvent(
    override var sequenceId: Int = 0,
    @SerialName("channel_id") val channelId: Snowflake,
    @SerialName("message_id") val messageId: Snowflake,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    val emoji: Emoji,
) : DispatchEvent()
