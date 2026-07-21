package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A reference from one message to another (replies, crossposts, forwards…), carried by
 * [Message.messageReference]. All fields are optional depending on the reference kind.
 *
 * @property type the reference type (`0` = default/reply, `1` = forward). Left untyped ([Int]) as Discord
 * keeps extending it.
 * @property messageId the referenced message id.
 * @property channelId the referenced channel id.
 * @property guildId the referenced guild id.
 * @property failIfNotExists whether sending should fail if the referenced message no longer exists.
 *
 * @see [Message Reference Object](https://discord.com/developers/docs/resources/message#message-reference-object)
 */
@Serializable
data class MessageReference(
    val type: Int? = null,
    @SerialName("message_id") val messageId: Snowflake? = null,
    @SerialName("channel_id") val channelId: Snowflake? = null,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    @SerialName("fail_if_not_exists") val failIfNotExists: Boolean? = null,
)
