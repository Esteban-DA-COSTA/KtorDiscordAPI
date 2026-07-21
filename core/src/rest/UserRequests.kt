package ktordiscord.core

import ktordiscord.components.CreateDMPayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Get the current user (the bot).
 *
 * @return an [HttpResponse] whose body is the current user.
 */
suspend fun DiscordClient.getCurrentUser(): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.USERS.text}/@me") {
        buildDiscordHeader(token)
    }
}

/**
 * Get a user by id.
 *
 * @param userId the id of the user.
 * @return an [HttpResponse] whose body is the user.
 */
suspend fun DiscordClient.getUser(userId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.USERS.text}/$userId") {
        buildDiscordHeader(token)
    }
}

/**
 * Open (or fetch the existing) DM channel with a user.
 *
 * @param recipientId the id of the user to open a DM with.
 * @return an [HttpResponse] whose body is the DM channel.
 */
suspend fun DiscordClient.createDM(recipientId: String): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.USERS.text}/@me/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(CreateDMPayload(recipientId))
    }
}
