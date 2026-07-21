package ktordiscord.core

import ktordiscord.components.CreateEmojiPayload
import ktordiscord.components.Emoji
import ktordiscord.components.ModifyEmojiPayload
import io.ktor.client.request.*
import io.ktor.http.*

private fun DiscordClient.emojisUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.EMOJIS.text}"

/**
 * List the custom emojis of a guild.
 *
 * @param guildId the id of the guild.
 * @return a [DiscordResponse] wrapping a list of emojis.
 */
suspend fun DiscordClient.listGuildEmojis(guildId: String): DiscordResponse<List<Emoji>> {
    return httpClient.get(emojisUrl(guildId)) {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Get a single guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji.
 * @return a [DiscordResponse] wrapping the emoji.
 */
suspend fun DiscordClient.getGuildEmoji(guildId: String, emojiId: String): DiscordResponse<Emoji> {
    return httpClient.get("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Create a custom emoji in a guild.
 *
 * @param guildId the id of the guild.
 * @param payload the emoji to create (name + base64 image data URI).
 * @return a [DiscordResponse] wrapping the created emoji.
 */
suspend fun DiscordClient.createGuildEmoji(guildId: String, payload: CreateEmojiPayload): DiscordResponse<Emoji> {
    return httpClient.post(emojisUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

/**
 * Modify a guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji to modify.
 * @param payload the fields to update.
 * @return a [DiscordResponse] wrapping the updated emoji.
 */
suspend fun DiscordClient.modifyGuildEmoji(guildId: String, emojiId: String, payload: ModifyEmojiPayload): DiscordResponse<Emoji> {
    return httpClient.patch("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

/**
 * Delete a guild emoji.
 *
 * @param guildId the id of the guild.
 * @param emojiId the id of the emoji to delete.
 */
suspend fun DiscordClient.deleteGuildEmoji(guildId: String, emojiId: String): DiscordResponse<Unit> {
    return httpClient.delete("${emojisUrl(guildId)}/$emojiId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}
