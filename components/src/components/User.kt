package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val username: String?,
    val discriminator: String?,
    val avatar: String?,
    val bot: Boolean?,
    val system: Boolean?,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean?,
    val banner: String?,
    @SerialName("accent_color") val accentColor: Int?,
    val locale: String?,
    val verified: Boolean?,
    val email: String?,
    val flags: Int?,
    @SerialName("premium_type") val premiumType: Int?,
    @SerialName("public_flags") val publicFlags: Int?
)
