package ktordiscord.gateway.events

import ktordiscord.components.Role
import ktordiscord.components.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GuildRoleCreateEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    val role: Role,
) : DispatchEvent()

@Serializable
data class GuildRoleUpdateEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    val role: Role,
) : DispatchEvent()

@Serializable
data class GuildRoleDeleteEvent(
    override var sequenceId: Int = 0,
    @SerialName("guild_id") val guildId: Snowflake,
    @SerialName("role_id") val roleId: Snowflake,
) : DispatchEvent()
