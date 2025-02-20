package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val user: components.User?,
    val nick: String?,
    val avatar: String?,
    val roles: List<String>?,
    @SerialName("joined_at")
    val joinedAt: String?,
    val deaf: Boolean = false,
    val mute: Boolean = false,
    val permissions: String?
)
