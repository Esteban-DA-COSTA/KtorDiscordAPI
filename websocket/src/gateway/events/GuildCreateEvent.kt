package gateway.events

import components.Guild

data class GuildCreateEvent(
    override var sequenceId: Int,
    val guild: Guild,
) : DispatchEvent()

