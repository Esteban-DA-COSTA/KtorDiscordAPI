package ktordiscord.core

import ktordiscord.components.BulkDeleteMessagesPayload
import ktordiscord.components.MessagePayload
import ktordiscord.components.ModifyChannelPayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

//#region Channel

/**
 * Get a channel by its id.
 *
 * @param channelId the id of the channel.
 * @return an [HttpResponse] whose body is the channel.
 */
suspend fun DiscordClient.getChannel(channelId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
    }
}

/**
 * Modify a channel's settings.
 *
 * @param channelId the id of the channel to modify.
 * @param payload the fields to update (unset fields are left unchanged).
 * @return an [HttpResponse] whose body is the updated channel.
 */
suspend fun DiscordClient.modifyChannel(channelId: String, payload: ModifyChannelPayload): HttpResponse {
    return httpClient.patch("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Delete a channel (or close a DM).
 *
 * @param channelId the id of the channel to delete.
 */
suspend fun DiscordClient.deleteChannel(channelId: String): HttpResponse {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
    }
}

//#endregion

//#region Messages

/**
 * Get a single message from a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @return an [HttpResponse] whose body is the message.
 */
suspend fun DiscordClient.getChannelMessage(channelId: String, messageId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
    }
}

/**
 * Get messages from a channel.
 *
 * The [around], [before] and [after] parameters are mutually exclusive message-id anchors.
 *
 * @param channelId the id of the channel.
 * @param around get messages around this message id.
 * @param before get messages before this message id.
 * @param after get messages after this message id.
 * @param limit max number of messages to return (1–100).
 * @return an [HttpResponse] whose body is a list of messages.
 */
suspend fun DiscordClient.getChannelMessages(
    channelId: String,
    around: String? = null,
    before: String? = null,
    after: String? = null,
    limit: Int? = null,
): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        around?.let { parameter("around", it) }
        before?.let { parameter("before", it) }
        after?.let { parameter("after", it) }
        limit?.let { parameter("limit", it) }
    }
}

/**
 * Edit a previously sent message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to edit.
 * @param message the new message content.
 * @return an [HttpResponse] whose body is the updated message.
 */
suspend fun DiscordClient.editChannelMessage(channelId: String, messageId: String, message: MessagePayload): HttpResponse {
    return httpClient.patch("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

/**
 * Delete a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to delete.
 */
suspend fun DiscordClient.deleteChannelMessage(channelId: String, messageId: String): HttpResponse {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
    }
}

/**
 * Bulk delete messages in a single request (2–100 ids, none older than two weeks).
 *
 * @param channelId the id of the channel.
 * @param messageIds the ids of the messages to delete.
 */
suspend fun DiscordClient.bulkDeleteMessages(channelId: String, messageIds: List<String>): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/bulk-delete") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(BulkDeleteMessagesPayload(messageIds))
    }
}

/**
 * Create a message in a channel.
 *
 * @param channelId the id of the channel.
 * @param message the message content.
 * @return an [HttpResponse] whose body is the created message.
 */
suspend fun DiscordClient.createChannelMessage(channelId: String, message: MessagePayload): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

//#endregion

//#region Pins

/**
 * Get all pinned messages of a channel.
 *
 * @param channelId the id of the channel.
 * @return an [HttpResponse] whose body is a list of pinned messages.
 */
suspend fun DiscordClient.getPinnedMessages(channelId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}") {
        buildDiscordHeader(token)
    }
}

/**
 * Pin a message in a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to pin.
 */
suspend fun DiscordClient.pinMessage(channelId: String, messageId: String): HttpResponse {
    return httpClient.put("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}/$messageId") {
        buildDiscordHeader(token)
    }
}

/**
 * Unpin a message from a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to unpin.
 */
suspend fun DiscordClient.unpinMessage(channelId: String, messageId: String): HttpResponse {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}/$messageId") {
        buildDiscordHeader(token)
    }
}

//#endregion

//#region Typing

/**
 * Trigger the typing indicator in a channel (lasts ~10 seconds).
 *
 * @param channelId the id of the channel.
 */
suspend fun DiscordClient.triggerTypingIndicator(channelId: String): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.TYPING.text}") {
        buildDiscordHeader(token)
    }
}

//#endregion
