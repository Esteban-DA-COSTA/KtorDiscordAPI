package ktordiscord.core

import ktordiscord.components.MessagePayload
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.Interaction

/** Callback registered for an application command, run with a [CommandInteractionScope] receiver. */
typealias CommandHandler = suspend CommandInteractionScope.() -> Unit

/** Callback registered for a message component (e.g. a button), run with a [ComponentInteractionScope] receiver. */
typealias ComponentHandler = suspend ComponentInteractionScope.() -> Unit

/**
 * Base scope handed to an interaction handler. Carries the incoming [interaction] and the plumbing
 * to reply to it. Concrete scopes ([CommandInteractionScope], [ComponentInteractionScope]) expose
 * the reply verbs that make sense for their interaction type.
 */
sealed class InteractionScope(
    val interaction: Interaction,
    internal val client: DiscordClient,
) {
    protected suspend fun reply(type: InteractionCallbackTypes, init: (ResponseScope.() -> Unit)?) {
        val payload = init?.let { ResponseScope(client).apply(it).build() }
        client.createInteractionResponse(interaction.id.value, interaction.token, type, payload)
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

/**
 * Typed key selecting the interaction registry to register a handler in — and, through its type
 * parameter [S], the handler's receiver scope. Using a typed key rather than a plain enum lets the
 * compiler infer the right scope per branch (`update { }` on components, etc.).
 *
 * @see DiscordClient.on
 */
sealed class InteractionKind<S : InteractionScope> {
    internal abstract fun register(client: DiscordClient, id: String, handler: suspend S.() -> Unit)

    /** Application commands, keyed by name. Receiver: [CommandInteractionScope]. */
    object Command : InteractionKind<CommandInteractionScope>() {
        override fun register(client: DiscordClient, id: String, handler: CommandHandler) =
            client.registerCommandHandler(id, handler)
    }

    /** Message components (buttons…), keyed by `custom_id`. Receiver: [ComponentInteractionScope]. */
    object Component : InteractionKind<ComponentInteractionScope>() {
        override fun register(client: DiscordClient, id: String, handler: ComponentHandler) =
            client.registerComponentHandler(id, handler)
    }
}

/** Scope for a `MESSAGE_COMPONENT` interaction (e.g. a button click). */
class ComponentInteractionScope internal constructor(
    interaction: Interaction,
    client: DiscordClient,
) : InteractionScope(interaction, client) {

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
