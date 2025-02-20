package components

import components.enums.StatusTypeEnum
import kotlinx.serialization.Serializable

@Serializable
data class Presence(
    var since: Int? = null,
    var activities: List<components.Activity> = mutableListOf(),
    var status: String = StatusTypeEnum.ONLINE.text,
    var afk: Boolean = false
)
