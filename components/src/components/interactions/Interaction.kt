package components.interactions

import components.*
import components.enums.ApplicationCommandOptionTypes
import components.enums.InteractionContextTypes
import components.enums.InteractionTypes
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Interaction(
    val id: Snowflake,
    @SerialName("application_id") val applicationId: Snowflake,
    val type: InteractionTypes?,
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
    val context: InteractionContextTypes?,
)