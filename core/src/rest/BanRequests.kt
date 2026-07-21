package ktordiscord.core

import ktordiscord.components.CreateBanPayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

private fun DiscordClient.bansUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.BANS.text}"

/**
 * Get the bans of a guild.
 *
 * @param guildId the id of the guild.
 * @param limit max number of bans to return (1–1000).
 * @param before get bans before this user id (pagination).
 * @param after get bans after this user id (pagination).
 * @return an [HttpResponse] whose body is a list of bans.
 */
suspend fun DiscordClient.getGuildBans(
    guildId: String,
    limit: Int? = null,
    before: String? = null,
    after: String? = null,
): HttpResponse {
    return httpClient.get(bansUrl(guildId)) {
        buildDiscordHeader(token)
        limit?.let { parameter("limit", it) }
        before?.let { parameter("before", it) }
        after?.let { parameter("after", it) }
    }
}

/**
 * Get a single ban entry for a user.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the banned user.
 * @return an [HttpResponse] whose body is the ban.
 */
suspend fun DiscordClient.getGuildBan(guildId: String, userId: String): HttpResponse {
    return httpClient.get("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }
}

/**
 * Ban a user from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the user to ban.
 * @param payload optional ban options (e.g. how much message history to delete).
 */
suspend fun DiscordClient.createGuildBan(guildId: String, userId: String, payload: CreateBanPayload? = null): HttpResponse {
    return httpClient.put("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        if (payload != null) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
    }
}

/**
 * Remove a ban from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the user to unban.
 */
suspend fun DiscordClient.removeGuildBan(guildId: String, userId: String): HttpResponse {
    return httpClient.delete("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }
}
