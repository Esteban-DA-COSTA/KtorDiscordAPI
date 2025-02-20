package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Snowflake,
    val filename: String,
    val description: String?,
    @SerialName("content_type")
    val contentType: String?,
    val size: Int,
    val url: String,
    @SerialName("proxy_url")
    val proxyUrl: String,
    val height: Int?,
    val width: Int?,
    val ephemeral: Boolean,
    @SerialName("duration_secs")
    val durationSecs: Float?,
    val waveform: String?,
    val flags: Int
)