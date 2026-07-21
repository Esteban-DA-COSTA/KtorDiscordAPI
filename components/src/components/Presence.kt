package ktordiscord.components

import ktordiscord.components.enums.StatusTypeEnum
import kotlinx.serialization.Serializable

/**
 * Payload `d` of an outgoing Presence Update (OP 3), also reused as the initial presence carried by
 * Identify. [status] serializes to its Discord string (`online`, `dnd`, `idle`…) and [activities]
 * uses the outgoing [BotActivity] model.
 */
@Serializable
data class Presence(
    var since: Int? = null,
    var activities: List<BotActivity> = emptyList(),
    var status: StatusTypeEnum = StatusTypeEnum.ONLINE,
    var afk: Boolean = false
)
