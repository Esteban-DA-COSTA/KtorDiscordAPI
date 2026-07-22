package ktordiscord.core

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.Snowflake
import ktordiscord.components.interactions.ApplicationCommandPayload

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
