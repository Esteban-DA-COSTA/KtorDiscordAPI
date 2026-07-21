package ktordiscord.builders

/**
 * DSL marker for the whole Discord builder DSL: message/response content (`MessagePayload`, `Embed`,
 * `Button`…), interaction and event scopes (`ResponseScope`, `CommandScope`, `InteractionScope`,
 * `EventScope`) and command definitions (`ApplicationCommandPayload`).
 *
 * Applied to the **receiver classes** (not the builder functions) so nested lambdas can't implicitly
 * reach an outer scope's members — e.g. inside `embed { }` you can't accidentally set a `ResponseScope`
 * field. Access an outer receiver explicitly with `this@respond` when genuinely needed.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class DiscordDsl
