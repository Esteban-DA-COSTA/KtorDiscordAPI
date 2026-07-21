package ktordiscord.core

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Builds the base reactions URL for a message: `.../messages/{messageId}/reactions`.
 */
private fun DiscordClient.reactionsUrl(channelId: String, messageId: String): String =
    "$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId/${DiscordEndpoints.REACTIONS.text}"

/**
 * Add a reaction to a message on behalf of the current user.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @param emoji the emoji, either a unicode character (e.g. `🔥`) or a custom emoji in `name:id` form.
 */
suspend fun DiscordClient.createReaction(channelId: String, messageId: String, emoji: String): HttpResponse {
    return httpClient.put("${reactionsUrl(channelId, messageId)}/${emoji.encodeEmoji()}/@me") {
        buildDiscordHeader(token)
    }
}

/**
 * Remove the current user's reaction from a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @param emoji the emoji (unicode character or custom emoji `name:id`).
 */
suspend fun DiscordClient.deleteOwnReaction(channelId: String, messageId: String, emoji: String): HttpResponse {
    return httpClient.delete("${reactionsUrl(channelId, messageId)}/${emoji.encodeEmoji()}/@me") {
        buildDiscordHeader(token)
    }
}

/**
 * Remove another user's reaction from a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @param emoji the emoji (unicode character or custom emoji `name:id`).
 * @param userId the id of the user whose reaction to remove.
 */
suspend fun DiscordClient.deleteUserReaction(channelId: String, messageId: String, emoji: String, userId: String): HttpResponse {
    return httpClient.delete("${reactionsUrl(channelId, messageId)}/${emoji.encodeEmoji()}/$userId") {
        buildDiscordHeader(token)
    }
}

/**
 * Get the users that reacted to a message with a given emoji.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @param emoji the emoji (unicode character or custom emoji `name:id`).
 * @param after get users after this user id (pagination).
 * @param limit max number of users to return (1–100).
 * @return an [HttpResponse] whose body is a list of users.
 */
suspend fun DiscordClient.getReactions(
    channelId: String,
    messageId: String,
    emoji: String,
    after: String? = null,
    limit: Int? = null,
): HttpResponse {
    return httpClient.get("${reactionsUrl(channelId, messageId)}/${emoji.encodeEmoji()}") {
        buildDiscordHeader(token)
        after?.let { parameter("after", it) }
        limit?.let { parameter("limit", it) }
    }
}

/**
 * Remove all reactions from a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 */
suspend fun DiscordClient.deleteAllReactions(channelId: String, messageId: String): HttpResponse {
    return httpClient.delete(reactionsUrl(channelId, messageId)) {
        buildDiscordHeader(token)
    }
}

/**
 * Remove all reactions of a specific emoji from a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @param emoji the emoji (unicode character or custom emoji `name:id`).
 */
suspend fun DiscordClient.deleteAllReactionsForEmoji(channelId: String, messageId: String, emoji: String): HttpResponse {
    return httpClient.delete("${reactionsUrl(channelId, messageId)}/${emoji.encodeEmoji()}") {
        buildDiscordHeader(token)
    }
}
