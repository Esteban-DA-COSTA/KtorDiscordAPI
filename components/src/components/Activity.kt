package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val name: String,
    val type: Int,
    val url: String?,
    @SerialName("created_at") val createdAt: Int,
    val timestamps: components.ActivityTimeStamp?,
    @SerialName("application_id") val applicationId: Long,
    val details: String?,
    val state: String?,
    val emoji: components.ActivityEmoji?,
    val party: components.ActivityParty?,
    val assets: components.ActivityAssets?,
    val secrets: components.ActivitySecret?,
    val instance: Boolean?,
    val flags: Int?,
    val buttons: components.ActivityButton?
)

@Serializable
data class ActivityEmoji(
    val name: String,
    val id: Long?,
    val animated: Boolean?
)

@Serializable
data class ActivityButton(
    val label: String,
    val url: String
)

@Serializable
data class ActivitySecret(
    val join: String?,
    val spectate: String?,
    val match: String?
)

@Serializable
data class ActivityAssets(
    @SerialName("large_image") val largeImage: String?,
    @SerialName("large_text") val largeText: String?,
    @SerialName("small_image") val smallImage: String?,
    @SerialName("small_text") val smallText: String?
)

@Serializable
data class ActivityParty(
    val id: String?,
    val size: Map<Int, Int>?
)

@Serializable
data class ActivityTimeStamp(
    val start: Int?,
    val end: Int?
)
