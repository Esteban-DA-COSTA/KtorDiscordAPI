package components

import kotlinx.serialization.Serializable

@Serializable
data class InstalParams(
    val scopes: List<String>,
    val permissions: String
)
