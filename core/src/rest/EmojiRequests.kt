package ktordiscord.core

import ktordiscord.components.CreateEmojiPayload
import ktordiscord.components.ModifyEmojiPayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

private fun DiscordClient.emojisUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.EMOJIS.text}"

/**
 * List the custom emojis of a guild.
 *
 * @param guildId the id of the guild.
 * @return an [HttpResponse] whose body is a list of emojis.
 */
suspend fun DiscordClient.listGuildEmojis(guildId: String): HttpResponse {
    return httpClient.get(emojisUrl(guildId)) {
        buildDiscordHeader(token)
    }
}

/**
 * Get a single guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji.
 * @return an [HttpResponse] whose body is the emoji.
 */
suspend fun DiscordClient.getGuildEmoji(guildId: String, emojiId: String): HttpResponse {
    return httpClient.get("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
    }
}

/**
 * Create a custom emoji in a guild.
 *
 * @param guildId the id of the guild.
 * @param payload the emoji to create (name + base64 image data URI).
 * @return an [HttpResponse] whose body is the created emoji.
 */
suspend fun DiscordClient.createGuildEmoji(guildId: String, payload: CreateEmojiPayload): HttpResponse {
    return httpClient.post(emojisUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Modify a guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji to modify.
 * @param payload the fields to update.
 * @return an [HttpResponse] whose body is the updated emoji.
 */
suspend fun DiscordClient.modifyGuildEmoji(guildId: String, emojiId: String, payload: ModifyEmojiPayload): HttpResponse {
    return httpClient.patch("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Delete a guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji to delete.
 */
suspend fun DiscordClient.deleteGuildEmoji(guildId: String, emojiId: String): HttpResponse {
    return httpClient.delete("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
    }
}
