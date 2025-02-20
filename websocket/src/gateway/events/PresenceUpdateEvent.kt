package gateway.events

import components.Activity
import components.ClientStatus
import components.User
import components.enums.StatusTypeEnum
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
