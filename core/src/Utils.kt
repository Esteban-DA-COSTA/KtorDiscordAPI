package ktordiscord.core

import io.ktor.client.request.*
import io.ktor.http.*

fun HttpRequestBuilder.buildDiscordHeader(token: String) {
    headers {
        append(HttpHeaders.Authorization, "Bot $token")
    }
}

/**
 * Percent-encodes an emoji for use in a reaction URL path segment.
 *
 * Unicode emojis (e.g. `🔥`) and custom emojis in the `name:id` form are both accepted: Discord
 * expects the value URL-encoded so multi-byte characters and the `:` separator survive the path.
 */
internal fun String.encodeEmoji(): String = this.encodeURLPathPart()