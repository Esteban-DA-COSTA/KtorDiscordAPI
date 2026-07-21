package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A user's voice connection state, as received in the `VOICE_STATE_UPDATE` dispatch event.
 *
 * A `null` [channelId] means the user left the voice channel.
 */
@Serializable
data class VoiceState(
    @SerialName("guild_id") val guildId: Snowflake? = null,
    @SerialName("channel_id") val channelId: Snowflake? = null,
    @SerialName("user_id") val userId: Snowflake,
    val member: Member? = null,
    @SerialName("session_id") val sessionId: String,
    val deaf: Boolean = false,
    val mute: Boolean = false,
    @SerialName("self_deaf") val selfDeaf: Boolean = false,
    @SerialName("self_mute") val selfMute: Boolean = false,
    @SerialName("self_stream") val selfStream: Boolean? = null,
    @SerialName("self_video") val selfVideo: Boolean = false,
    val suppress: Boolean = false,
    @SerialName("request_to_speak_timestamp") val requestToSpeakTimestamp: String? = null,
)
