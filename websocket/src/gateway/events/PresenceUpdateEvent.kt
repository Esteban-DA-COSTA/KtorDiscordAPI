package ktordiscord.gateway.events

import ktordiscord.components.Activity
import ktordiscord.components.ClientStatus
import ktordiscord.components.User
import ktordiscord.components.enums.StatusTypeEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresenceUpdateEvent(
    override var sequenceId: Int = 0,
    val user: User,
    @SerialName("guild_id")
    val guildId: Long,
    val status: StatusTypeEnum,
    val activities: List<Activity>,
    @SerialName("client_id")
    val clientStatus: ClientStatus?
) : DispatchEvent()
