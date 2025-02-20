package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    val id: Long,
    val name: String?,
    val icon: String?,
    val description: String?,
    @SerialName("rpc_origins") val rpcOrigins: List<String>?,
    @SerialName("bot_public") val botPublic: Boolean?,
    @SerialName("bot_require_code_grant") val botRequireCodeGrant: Boolean?,
    @SerialName("terms_of_service_url") val termsOfServiceUrl: String?,
    @SerialName("privacy_policy_url") val privacyPolicyUrl: String?,
    val owner: components.User?,
    @SerialName("verify_key") val verifyKey: String?,
    val team: components.Team?,
    @SerialName("guild_id") val guildId: Long?,
    @SerialName("primary_sku_id") val primarySkuId: Long?,
    val slug: String?,
    @SerialName("cover_image") val coverImage: String?,
    val flags: Int?,
    val tags: List<String>?,
    @SerialName("install_params") val installParams: components.InstalParams?,
    @SerialName("custom_install_url") val customInstallUrl: String?,
    @SerialName("role_connections_verification_url") val roleConnectionsVerificationUrl: String?
    )
