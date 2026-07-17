package ktordiscord.components

import ktordiscord.components.enums.ChannelTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: ktordiscord.components.Snowflake,
    val type: ktordiscord.components.enums.ChannelTypes,
    @SerialName("guild_id")
    val guildId: ktordiscord.components.Snowflake?,
    val position: Int,
    val name: String?,
    val topic: String?,
    val nsfw: Boolean,
    @SerialName("last_message_id")
    val lastMessageId: ktordiscord.components.Snowflake?,
    val bitrate: Int?,
    @SerialName("user_limit")
    val userLimit: Int?,
    val recipients: List<ktordiscord.components.User>?,
)
