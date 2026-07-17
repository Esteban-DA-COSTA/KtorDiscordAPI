package ktordiscord.gateway.events

import ktordiscord.components.Guild

data class GuildCreateEvent(
    override var sequenceId: Int,
    val guild: Guild,
) : DispatchEvent()

