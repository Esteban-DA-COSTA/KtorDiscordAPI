package ktordiscord.components.interactions

import ktordiscord.components.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Interaction(
    val id: Snowflake,
    @SerialName("application_id") val applicationId: Snowflake,
    val type: ktordiscord.components.enums.InteractionTypes?,
    @Transient var data: InteractionData? = null,
    @SerialName("guild_id") val guildId: Snowflake?,
    val channel: Channel?,
    @SerialName("channel_id") val channelId: Snowflake?,
    val member: Member?,
    val user: User?,
    val token: String,
    val version: Int = 1,
    val message: Message?,
    @SerialName("app_permissions") val appPermissions: String?,
    val locale: String?,
    @SerialName("guild_locale") val guildLocale: String?,
    val entitlements: List<Entitlement>?,
    @SerialName("authorizing_integration_owners") val authorizingIntegrationOwners: Map<String, String>?,
    val context: ktordiscord.components.enums.InteractionContextTypes?,
)
