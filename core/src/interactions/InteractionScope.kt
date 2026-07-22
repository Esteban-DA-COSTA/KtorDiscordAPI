package ktordiscord.core

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.Interaction

/**
 * Separator between a component's routing key (prefix) and its state payload inside a `custom_id`.
 * `on(InteractionKind.Component, "approve")` matches every click whose id is `"approve"` or starts
 * with `"approve:"`; the part after the first separator is exposed as [ComponentInteractionScope.arg].
 * Reserved: a routing key registered via `on(...)` must not contain it.
 */
internal const val COMPONENT_ID_SEPARATOR = ':'

/** Callback registered for an application command, run with a [CommandInteractionScope] receiver. */
typealias CommandHandler = suspend CommandInteractionScope.() -> Unit

/** Callback registered for a message component (e.g. a button), run with a [ComponentInteractionScope] receiver. */
typealias ComponentHandler = suspend ComponentInteractionScope.() -> Unit

/**
 * Base scope handed to an interaction handler. Carries the incoming [interaction] and the plumbing
 * to reply to it. Concrete scopes ([CommandInteractionScope], [ComponentInteractionScope]) expose
 * the reply verbs that make sense for their interaction type.
 */
@DiscordDsl
sealed class InteractionScope(
    val interaction: Interaction,
    internal val client: DiscordClient,
) {
    protected suspend fun reply(type: InteractionCallbackTypes, init: (ResponseScope.() -> Unit)?) {
        val payload = init?.let { ResponseScope(client).apply(it).build() }
        client.createInteractionResponse(interaction.id, interaction.token, type, payload)
    }

    /**
     * Edit the original response (the message sent by the first `respond`/`update`, or the pending
     * message after [CommandInteractionScope.defer]). Targets the interaction webhook.
     */
    suspend fun editOriginal(init: ResponseScope.() -> Unit) {
        val payload = ResponseScope(client).apply(init).build()
        client.editOriginalInteractionResponse(client.applicationId, interaction.token, payload)
    }
}

/** Scope for an `APPLICATION_COMMAND` interaction (a slash command invocation). */
class CommandInteractionScope internal constructor(
    interaction: Interaction,
    client: DiscordClient,
) : InteractionScope(interaction, client) {

    /** Send a brand-new message as the command's response (`CHANNEL_MESSAGE_WITH_SOURCE`). */
    suspend fun respond(init: ResponseScope.() -> Unit) =
        reply(InteractionCallbackTypes.CHANNEL_MESSAGE_WITH_SOURCE, init)

    /**
     * Acknowledge now and reply later (`DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE`). Discord shows a
     * "thinking…" state; fill in the message afterwards with [editOriginal]. Required when the real
     * work takes more than 3 seconds.
     */
    suspend fun defer() =
        reply(InteractionCallbackTypes.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, null)
}

/** Scope for a `MESSAGE_COMPONENT` interaction (e.g. a button click). */
class ComponentInteractionScope internal constructor(
    interaction: Interaction,
    client: DiscordClient,
    /** The raw `custom_id` of the clicked component (routing prefix + optional state payload). */
    val customId: String,
) : InteractionScope(interaction, client) {

    /**
     * The state payload carried after the first [COMPONENT_ID_SEPARATOR] in [customId], or `""` when
     * the id carries none. For a `"approve:42"` click registered under `"approve"`, this is `"42"`.
     */
    val arg: String get() = customId.substringAfter(COMPONENT_ID_SEPARATOR, "")

    /** Reply with a new message (`CHANNEL_MESSAGE_WITH_SOURCE`), leaving the source message intact. */
    suspend fun respond(init: ResponseScope.() -> Unit) =
        reply(InteractionCallbackTypes.CHANNEL_MESSAGE_WITH_SOURCE, init)

    /** Edit the message the component belongs to (`UPDATE_MESSAGE`). */
    suspend fun update(init: ResponseScope.() -> Unit) =
        reply(InteractionCallbackTypes.UPDATE_MESSAGE, init)

    /** Acknowledge the click without an immediate visible change (`DEFERRED_UPDATE_MESSAGE`). */
    suspend fun defer() =
        reply(InteractionCallbackTypes.DEFERRED_UPDATE_MESSAGE, null)
}
