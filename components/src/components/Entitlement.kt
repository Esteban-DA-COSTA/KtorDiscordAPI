package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Entitlement(
    val id: components.Snowflake,
    @SerialName("sku_id")
    val skuId: components.Snowflake,
    @SerialName("application_id")
    val applicationId: components.Snowflake,
    @SerialName("user_id")
    val userId: components.Snowflake?,
    val type: Int,
    val deleted: Boolean,
    @SerialName("starts_at")
    val startsAt: String?,
    @SerialName("ends_at")
    val endsAt: String?,
    @SerialName("guild_id")
    val guildId: components.Snowflake?
)
