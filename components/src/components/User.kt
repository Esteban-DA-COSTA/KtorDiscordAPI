package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Snowflake,
    val username: String? = null,
    val discriminator: String? = null,
    val avatar: String? = null,
    val bot: Boolean? = null,
    val system: Boolean? = null,
    @SerialName("mfa_enabled") val mfaEnabled: Boolean? = null,
    val banner: String? = null,
    @SerialName("accent_color") val accentColor: Int? = null,
    val locale: String? = null,
    val verified: Boolean? = null,
    val email: String? = null,
    val flags: Int? = null,
    @SerialName("premium_type") val premiumType: Int? = null,
    @SerialName("public_flags") val publicFlags: Int? = null
)

/**
 * Payload used to **open a DM** channel with a user (POST `/users/@me/channels`).
 *
 * @property recipientId the id of the user to open a DM with.
 */
@Serializable
data class CreateDMPayload(
    @SerialName("recipient_id") var recipientId: Snowflake,
)
