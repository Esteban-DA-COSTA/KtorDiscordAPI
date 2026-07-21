package ktordiscord.components

import ktordiscord.components.enums.ActivityType
import kotlinx.serialization.Serializable

/**
 * Lightweight outgoing activity used to advertise the bot's presence (OP 3 Presence Update).
 *
 * Unlike the inbound [Activity] model — which Discord fully populates — an outgoing activity only
 * carries the few fields a bot may set. [url] is honored only for [ActivityType.STREAMING], and
 * [state] holds the text of an [ActivityType.CUSTOM] status.
 *
 * @see [Activity Object](https://discord.com/developers/docs/events/gateway-events#activity-object)
 */
@Serializable
data class BotActivity(
    val name: String,
    val type: ActivityType,
    val url: String? = null,
    val state: String? = null
)
