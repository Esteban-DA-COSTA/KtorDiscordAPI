package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sticker(
    val id: Snowflake,
    @SerialName("pack_id") val packId: Snowflake,
    val name: String,
    val description: String?,
    val tags: String, // A comma separated list
    val asset: String?,
    val type: Int,
    @SerialName("format_type") val formatType: Int,
    val available: Boolean?,
    @SerialName("guild_id") val guildId: Snowflake?,
    val user: User?,
    @SerialName("sort_value") val sortValue: Int
)
