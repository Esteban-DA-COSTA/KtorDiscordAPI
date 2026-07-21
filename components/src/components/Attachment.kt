package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Snowflake,
    val filename: String,
    val description: String? = null,
    @SerialName("content_type")
    val contentType: String? = null,
    val size: Int,
    val url: String,
    @SerialName("proxy_url")
    val proxyUrl: String,
    val height: Int? = null,
    val width: Int? = null,
    val ephemeral: Boolean = false,
    @SerialName("duration_secs")
    val durationSecs: Float? = null,
    val waveform: String? = null,
    val flags: Int = 0
)