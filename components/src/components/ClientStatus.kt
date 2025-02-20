package components

import kotlinx.serialization.Serializable

@Serializable
data class ClientStatus(
    val desktop: String?,
    val mobile: String?,
    val web: String?
)
