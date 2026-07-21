package ktordiscord.gateway.events

import ktordiscord.components.Presence
import ktordiscord.gateway.OPCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Outgoing Presence Update command (OP 3): advertises the bot's status and activities.
 *
 * Not to be confused with [PresenceUpdateEvent], the inbound dispatch event of the same Discord name.
 *
 * @see [Update Presence](https://discord.com/developers/docs/events/gateway-events#update-presence)
 */
@Serializable(UpdatePresenceSerializer::class)
class UpdatePresenceEvent(presence: Presence) : Event() {

    @SerialName("d")
    val data = presence
}

object UpdatePresenceSerializer : OutgoingEventSerializer<UpdatePresenceEvent, Presence>(
    "presenceUpdate", OPCode.PRESENCE_UPDATE, Presence.serializer(), { it.data }
)
