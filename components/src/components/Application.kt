package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Application(
    val id: Snowflake,
    val name: String?,
    val icon: String?,
    val description: String?,
    @SerialName("rpc_origins") val rpcOrigins: List<String>?,
    @SerialName("bot_public") val botPublic: Boolean?,
    @SerialName("bot_require_code_grant") val botRequireCodeGrant: Boolean?,
    @SerialName("terms_of_service_url") val termsOfServiceUrl: String?,
    @SerialName("privacy_policy_url") val privacyPolicyUrl: String?,
    val owner: User?,
    @SerialName("verify_key") val verifyKey: String?,
    val team: Team?,
    @SerialName("guild_id") val guildId: Snowflake?,
    @SerialName("primary_sku_id") val primarySkuId: Snowflake?,
    val slug: String?,
    @SerialName("cover_image") val coverImage: String?,
    val flags: Int?,
    val tags: List<String>?,
    @SerialName("install_params") val installParams: InstalParams?,
    @SerialName("custom_install_url") val customInstallUrl: String?,
    @SerialName("role_connections_verification_url") val roleConnectionsVerificationUrl: String?
    )
