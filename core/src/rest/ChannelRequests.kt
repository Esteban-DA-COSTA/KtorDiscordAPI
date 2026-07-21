package ktordiscord.core

import ktordiscord.components.MessagePayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun DiscordClient.createChannelMessage(channelId: String, message: MessagePayload): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}
