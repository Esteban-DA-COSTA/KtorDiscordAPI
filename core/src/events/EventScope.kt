package ktordiscord.core

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.Message
import ktordiscord.gateway.events.DispatchEvent
import ktordiscord.gateway.events.MessageCreateEvent
import ktordiscord.gateway.events.MessageUpdateEvent

/**
 * Receiver handed to an event handler registered with [DiscordClient.on]. Carries the incoming
 * [event] and the [client] that received it. Convenience reply verbs are provided as typed
 * extensions on this scope (see [reply]) for the events that make them meaningful.
 *
 * Mirrors the interaction router's `InteractionScope` (which exposes an `interaction` property).
 */
@DiscordDsl
class EventScope<out T : DispatchEvent> internal constructor(
    val event: T,
    internal val client: DiscordClient,
)

/**
 * Reply to the message that triggered this event by sending a new message in its channel. Uses the
 * shared [ResponseScope], so the reply can carry interactive components — e.g. a button whose click
 * callback is bound inline with `button(...).click { }`.
 *
 * @throws IllegalStateException if the incoming message carries no channel id.
 */
@JvmName("replyToMessageCreate")
suspend fun EventScope<MessageCreateEvent>.reply(init: ResponseScope.() -> Unit): DiscordResponse<Message> {
    val channelId = event.message.channelId ?: error("Message has no channel id")
    return client.sendMessage(channelId, init)
}

/**
 * Reply to the edited message that triggered this event by sending a new message in its channel.
 *
 * @throws IllegalStateException if the incoming message carries no channel id.
 * @see reply
 */
@JvmName("replyToMessageUpdate")
suspend fun EventScope<MessageUpdateEvent>.reply(init: ResponseScope.() -> Unit): DiscordResponse<Message> {
    val channelId = event.message.channelId ?: error("Message has no channel id")
    return client.sendMessage(channelId, init)
}
