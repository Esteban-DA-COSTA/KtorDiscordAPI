package ktordiscord.gateway.events

import ktordiscord.components.Guild
import ktordiscord.components.UnavailableGuild
import kotlinx.serialization.Serializable

@Serializable
data class GuildUpdateEvent(
    override var sequenceId: Int,
    val guild: Guild,
) : DispatchEvent()

/**
 * `GUILD_DELETE`: the bot was removed from a guild, or the guild became unavailable (outage).
 * When [UnavailableGuild.unavailable] is `true` the guild is unreachable; otherwise the bot left.
 */
@Serializable
data class GuildDeleteEvent(
    override var sequenceId: Int,
    val guild: UnavailableGuild,
) : DispatchEvent()
