package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entitlement(
    val id: ktordiscord.components.Snowflake,
    @SerialName("sku_id")
    val skuId: ktordiscord.components.Snowflake,
    @SerialName("application_id")
    val applicationId: ktordiscord.components.Snowflake,
    @SerialName("user_id")
    val userId: ktordiscord.components.Snowflake?,
    val type: Int,
    val deleted: Boolean,
    @SerialName("starts_at")
    val startsAt: String?,
    @SerialName("ends_at")
    val endsAt: String?,
    @SerialName("guild_id")
    val guildId: ktordiscord.components.Snowflake?
)
