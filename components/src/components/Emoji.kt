package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val id: Long?,
    val name: String?,
    val roles: List<components.Role>?,
    val user: components.User?,
    @SerialName("require_colons") val requireColons: Boolean?,
    val managed: Boolean?,
    val animated: Boolean?,
    val available: Boolean?
)
