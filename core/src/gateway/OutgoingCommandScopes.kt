package ktordiscord.core

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.BotActivity
import ktordiscord.components.Presence
import ktordiscord.components.enums.ActivityType
import ktordiscord.components.enums.StatusTypeEnum
import ktordiscord.gateway.events.RequestGuildMembersData

/**
 * DSL scope building the [Presence] sent by [DiscordClient.updatePresence] (OP 3).
 *
 * Set [status]/[afk], then declare at most one activity with a helper (`playing`, `watching`…);
 * each replaces the previous one, matching Discord's single-activity limit for bots.
 *
 * ```
 * client.updatePresence {
 *     status = StatusTypeEnum.DO_NOT_DISTURB
 *     watching("the gateway")
 * }
 * ```
 */
@DiscordDsl
class PresenceScope internal constructor() {
    /** Bot status (`online`, `dnd`, `idle`, `invisible`). */
    var status: StatusTypeEnum = StatusTypeEnum.ONLINE

    /** Whether the bot is flagged away-from-keyboard. */
    var afk: Boolean = false

    /** Unix time (ms) since when the bot has been idle, or `null`. */
    var since: Int? = null

    private var activity: BotActivity? = null

    /** "Playing {name}". */
    fun playing(name: String) {
        activity = BotActivity(name, ActivityType.PLAYING)
    }

    /** "Streaming {name}", linking to [url] (Twitch/YouTube). */
    fun streaming(name: String, url: String) {
        activity = BotActivity(name, ActivityType.STREAMING, url = url)
    }

    /** "Listening to {name}". */
    fun listening(name: String) {
        activity = BotActivity(name, ActivityType.LISTENING)
    }

    /** "Watching {name}". */
    fun watching(name: String) {
        activity = BotActivity(name, ActivityType.WATCHING)
    }

    /** "Competing in {name}". */
    fun competing(name: String) {
        activity = BotActivity(name, ActivityType.COMPETING)
    }

    /** Custom status showing [text] verbatim (no "Playing/Watching…" prefix). */
    fun custom(text: String) {
        activity = BotActivity(text, ActivityType.CUSTOM, state = text)
    }

    internal fun build(): Presence = Presence(
        since = since,
        activities = activity?.let { listOf(it) } ?: emptyList(),
        status = status,
        afk = afk
    )
}

/**
 * DSL scope building the [RequestGuildMembersData] sent by [DiscordClient.requestGuildMembers] (OP 8).
 *
 * Leave [query]/[limit] at their defaults (`""` / `0`) to request every member, set [query] to a name
 * prefix to search, or set [userIds] to fetch specific members. Requires the `GUILD_MEMBERS` intent.
 */
@DiscordDsl
class RequestGuildMembersScope internal constructor(private val guildId: String) {
    /** Name prefix to match; empty string matches everyone. Ignored when [userIds] is set. */
    var query: String = ""

    /** Max number of members to return (0 = no limit, only valid with an empty [query]). */
    var limit: Int = 0

    /** Whether to include presences of the returned members. */
    var presences: Boolean? = null

    /** Specific member ids to fetch, instead of a [query]. */
    var userIds: List<String>? = null

    /** Client-chosen token echoed back on the resulting chunks, to correlate the response. */
    var nonce: String? = null

    internal fun build(): RequestGuildMembersData = RequestGuildMembersData(
        guildId = guildId,
        query = if (userIds == null) query else null,
        limit = limit,
        presences = presences,
        userIds = userIds,
        nonce = nonce
    )
}
