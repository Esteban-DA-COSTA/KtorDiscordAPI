package ktordiscord.core

import ktordiscord.components.Channel
import ktordiscord.components.CreateDMPayload
import ktordiscord.components.Snowflake
import ktordiscord.components.User
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Get the current user (the bot).
 *
 * @return a [DiscordResponse] wrapping the current user.
 */
suspend fun DiscordClient.getCurrentUser(): DiscordResponse<User> {
    return httpClient.get("$discordURL/${DiscordEndpoints.USERS.text}/@me") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Get a user by id.
 *
 * @param userId the id of the user.
 * @return a [DiscordResponse] wrapping the user.
 */
suspend fun DiscordClient.getUser(userId: Snowflake): DiscordResponse<User> {
    return httpClient.get("$discordURL/${DiscordEndpoints.USERS.text}/$userId") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Open (or fetch the existing) DM channel with a user.
 *
 * @param recipientId the id of the user to open a DM with.
 * @return a [DiscordResponse] wrapping the DM channel.
 */
suspend fun DiscordClient.createDM(recipientId: Snowflake): DiscordResponse<Channel> {
    return httpClient.post("$discordURL/${DiscordEndpoints.USERS.text}/@me/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(CreateDMPayload(recipientId))
    }.decode()
}
