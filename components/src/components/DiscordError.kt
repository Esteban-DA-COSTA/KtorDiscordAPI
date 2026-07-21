package ktordiscord.components

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * The JSON error body Discord returns on a 4xx/5xx REST response.
 *
 * @property code Discord's numeric error code (distinct from the HTTP status).
 * @property message the human-readable error message.
 * @property errors the (optional) per-field validation details; its shape varies per endpoint, so
 *   it is kept as a raw [JsonElement].
 *
 * @see [Discord API error reference](https://discord.com/developers/docs/topics/opcodes-and-status-codes#json)
 */
@Serializable
data class DiscordError(
    val code: Int,
    val message: String,
    val errors: JsonElement? = null,
)
