package components

import components.enums.ChannelTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: components.Snowflake,
    val type: components.enums.ChannelTypes,
    @SerialName("guild_id")
    val guildId: components.Snowflake?,
    val position: Int,
    val name: String?,
    val topic: String?,
    val nsfw: Boolean,
    @SerialName("last_message_id")
    val lastMessageId: components.Snowflake?,
    val bitrate: Int?,
    @SerialName("user_limit")
    val userLimit: Int?,
    val recipients: List<components.User>?,
)
