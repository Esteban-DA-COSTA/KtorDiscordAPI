package ktordiscord.core

import ktordiscord.components.Ban
import ktordiscord.components.CreateBanPayload
import ktordiscord.components.Snowflake
import io.ktor.client.request.*
import io.ktor.http.*

private fun DiscordClient.bansUrl(guildId: Snowflake): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.BANS.text}"

/**
 * Get the bans of a guild.
 *
 * @param guildId the id of the guild.
 * @param limit max number of bans to return (1–1000).
 * @param before get bans before this user id (pagination).
 * @param after get bans after this user id (pagination).
 * @return a [DiscordResponse] wrapping a list of bans.
 */
suspend fun DiscordClient.getGuildBans(
    guildId: Snowflake,
    limit: Int? = null,
    before: Snowflake? = null,
    after: Snowflake? = null,
): DiscordResponse<List<Ban>> {
    return httpClient.get(bansUrl(guildId)) {
        buildDiscordHeader(token)
        limit?.let { parameter("limit", it) }
        before?.let { parameter("before", it) }
        after?.let { parameter("after", it) }
    }.decode()
}

/**
 * Get a single ban entry for a user.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the banned user.
 * @return a [DiscordResponse] wrapping the ban.
 */
suspend fun DiscordClient.getGuildBan(guildId: Snowflake, userId: Snowflake): DiscordResponse<Ban> {
    return httpClient.get("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Ban a user from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the user to ban.
 * @param payload optional ban options (e.g. how much message history to delete).
 */
suspend fun DiscordClient.createGuildBan(guildId: Snowflake, userId: Snowflake, payload: CreateBanPayload? = null): DiscordResponse<Unit> {
    return httpClient.put("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        if (payload != null) {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
    }.decodeEmpty()
}

/**
 * Remove a ban from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the user to unban.
 */
suspend fun DiscordClient.removeGuildBan(guildId: Snowflake, userId: Snowflake): DiscordResponse<Unit> {
    return httpClient.delete("${bansUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}
