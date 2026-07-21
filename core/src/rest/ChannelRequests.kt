package ktordiscord.core

import ktordiscord.components.BulkDeleteMessagesPayload
import ktordiscord.components.Channel
import ktordiscord.components.Message
import ktordiscord.components.MessagePayload
import ktordiscord.components.ModifyChannelPayload
import ktordiscord.components.Snowflake
import io.ktor.client.request.*
import io.ktor.http.*

//#region Channel

/**
 * Get a channel by its id.
 *
 * @param channelId the id of the channel.
 * @return a [DiscordResponse] wrapping the channel.
 */
suspend fun DiscordClient.getChannel(channelId: Snowflake): DiscordResponse<Channel> {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Modify a channel's settings.
 *
 * @param channelId the id of the channel to modify.
 * @param payload the fields to update (unset fields are left unchanged).
 * @return a [DiscordResponse] wrapping the updated channel.
 */
suspend fun DiscordClient.modifyChannel(channelId: Snowflake, payload: ModifyChannelPayload): DiscordResponse<Channel> {
    return httpClient.patch("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

/**
 * Delete a channel (or close a DM).
 *
 * @param channelId the id of the channel to delete.
 */
suspend fun DiscordClient.deleteChannel(channelId: Snowflake): DiscordResponse<Unit> {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

//#endregion

//#region Messages

/**
 * Get a single message from a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message.
 * @return a [DiscordResponse] wrapping the message.
 */
suspend fun DiscordClient.getChannelMessage(channelId: Snowflake, messageId: Snowflake): DiscordResponse<Message> {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
    }.decode()
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
 * @return a [DiscordResponse] wrapping a list of messages.
 */
suspend fun DiscordClient.getChannelMessages(
    channelId: Snowflake,
    around: Snowflake? = null,
    before: Snowflake? = null,
    after: Snowflake? = null,
    limit: Int? = null,
): DiscordResponse<List<Message>> {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        around?.let { parameter("around", it) }
        before?.let { parameter("before", it) }
        after?.let { parameter("after", it) }
        limit?.let { parameter("limit", it) }
    }.decode()
}

/**
 * Edit a previously sent message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to edit.
 * @param message the new message content.
 * @return a [DiscordResponse] wrapping the updated message.
 */
suspend fun DiscordClient.editChannelMessage(channelId: Snowflake, messageId: Snowflake, message: MessagePayload): DiscordResponse<Message> {
    return httpClient.patch("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }.decode()
}

/**
 * Delete a message.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to delete.
 */
suspend fun DiscordClient.deleteChannelMessage(channelId: Snowflake, messageId: Snowflake): DiscordResponse<Unit> {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/$messageId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

/**
 * Bulk delete messages in a single request (2–100 ids, none older than two weeks).
 *
 * @param channelId the id of the channel.
 * @param messageIds the ids of the messages to delete.
 */
suspend fun DiscordClient.bulkDeleteMessages(channelId: Snowflake, messageIds: List<Snowflake>): DiscordResponse<Unit> {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}/bulk-delete") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(BulkDeleteMessagesPayload(messageIds))
    }.decodeEmpty()
}

/**
 * Create a message in a channel.
 *
 * @param channelId the id of the channel.
 * @param message the message content.
 * @return a [DiscordResponse] wrapping the created message.
 */
suspend fun DiscordClient.createChannelMessage(channelId: Snowflake, message: MessagePayload): DiscordResponse<Message> {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }.decode()
}

//#endregion

//#region Pins

/**
 * Get all pinned messages of a channel.
 *
 * @param channelId the id of the channel.
 * @return a [DiscordResponse] wrapping a list of pinned messages.
 */
suspend fun DiscordClient.getPinnedMessages(channelId: Snowflake): DiscordResponse<List<Message>> {
    return httpClient.get("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Pin a message in a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to pin.
 */
suspend fun DiscordClient.pinMessage(channelId: Snowflake, messageId: Snowflake): DiscordResponse<Unit> {
    return httpClient.put("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}/$messageId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

/**
 * Unpin a message from a channel.
 *
 * @param channelId the id of the channel.
 * @param messageId the id of the message to unpin.
 */
suspend fun DiscordClient.unpinMessage(channelId: Snowflake, messageId: Snowflake): DiscordResponse<Unit> {
    return httpClient.delete("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.PINS.text}/$messageId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

//#endregion

//#region Typing

/**
 * Trigger the typing indicator in a channel (lasts ~10 seconds).
 *
 * @param channelId the id of the channel.
 */
suspend fun DiscordClient.triggerTypingIndicator(channelId: Snowflake): DiscordResponse<Unit> {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.TYPING.text}") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

//#endregion
