package ktordiscord.core

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.Snowflake
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.ApplicationCommandPayload
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

/**
 * Configuration scope for `client.on("commandName") { ... }`. Runs **once at registration** (unlike
 * [CommandInteractionScope], which is instantiated per dispatched interaction). It lets a single `on`
 * block both **declare** the command on Discord ([define]) and register how it is **handled** when
 * invoked ([handle] / [respond] / [defer]).
 *
 * ```
 * client.on("ping") {
 *     define { description = "Ping the bot" }   // synced to Discord on login()
 *     respond { content = "pong" }              // static reply
 * }
 *
 * client.on("echo") {
 *     define { description = "Echo back" }
 *     handle {                                  // full CommandInteractionScope: dynamic
 *         defer(); editOriginal { content = interaction.token }
 *     }
 * }
 * ```
 */
@DiscordDsl
class CommandScope internal constructor(
    private val client: DiscordClient,
    private val commandName: String,
) {
    /**
     * Declare this command on Discord. The definition is collected and pushed on [DiscordClient.login]
     * via a **bulk overwrite** (one request per scope). Omit [guildId] for a global command; pass a
     * guild id for a guild command (propagates instantly — handy for development).
     *
     * Because the sync uses bulk overwrite, commands **not** declared with `define` in the same scope
     * are removed from Discord on login (declarative registration, per Discord's startup guidance).
     */
    fun define(guildId: Snowflake? = null, init: ApplicationCommandPayload.() -> Unit) =
        client.registerCommandDefinition(commandName, guildId, init)

    /**
     * Register the dynamic dispatch handler, run with a full [CommandInteractionScope] receiver
     * (access to `interaction`, `respond`, `defer`, `editOriginal`, branching…). This is the general
     * form; [respond] and [defer] are shorthands for common cases.
     */
    fun handle(handler: CommandHandler) =
        client.registerCommandHandler(commandName, handler)

    /** Shorthand for the common static reply. Equivalent to `handle { respond(init) }`. */
    fun respond(init: ResponseScope.() -> Unit) =
        client.registerCommandHandler(commandName) { respond(init) }

    /**
     * Shorthand acknowledging the command in a "thinking…" state; complete it later with
     * [InteractionScope.editOriginal] (use [handle] if that follow-up needs the interaction).
     * Equivalent to `handle { defer() }`.
     */
    fun defer() =
        client.registerCommandHandler(commandName) { defer() }
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

    /**
     * Message components (buttons…), keyed by the **prefix** of the `custom_id` (the part before the
     * first [COMPONENT_ID_SEPARATOR], which is the whole id when it has none). Receiver:
     * [ComponentInteractionScope]; the state after the separator is read via [ComponentInteractionScope.arg].
     */
    object Component : InteractionKind<ComponentInteractionScope>() {
        override fun register(client: DiscordClient, id: String, handler: ComponentHandler) =
            client.registerComponentHandler(id, handler)
    }
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
