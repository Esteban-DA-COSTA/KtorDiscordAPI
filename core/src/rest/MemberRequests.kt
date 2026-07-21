package ktordiscord.core

import ktordiscord.components.AddMemberPayload
import ktordiscord.components.ModifyMemberPayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

private fun DiscordClient.membersUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.MEMBERS.text}"

/**
 * List members of a guild.
 *
 * @param guildId the id of the guild.
 * @param limit max number of members to return (1–1000).
 * @param after get members after this user id (pagination).
 * @return an [HttpResponse] whose body is a list of members.
 */
suspend fun DiscordClient.listGuildMembers(guildId: String, limit: Int? = null, after: String? = null): HttpResponse {
    return httpClient.get(membersUrl(guildId)) {
        buildDiscordHeader(token)
        limit?.let { parameter("limit", it) }
        after?.let { parameter("after", it) }
    }
}

/**
 * Get a single guild member.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 * @return an [HttpResponse] whose body is the member.
 */
suspend fun DiscordClient.getGuildMember(guildId: String, userId: String): HttpResponse {
    return httpClient.get("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }
}

/**
 * Add a user to a guild.
 *
 * Requires an OAuth2 access token (in [AddMemberPayload.accessToken]) granted the `guilds.join`
 * scope for the target user — this is **not** the bot token.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the user to add.
 * @param payload the join payload (access token and optional overrides).
 * @return an [HttpResponse] whose body is the member (or `204` if already a member).
 */
suspend fun DiscordClient.addGuildMember(guildId: String, userId: String, payload: AddMemberPayload): HttpResponse {
    return httpClient.put("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Modify attributes of a guild member.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 * @param payload the fields to update.
 * @return an [HttpResponse] whose body is the updated member.
 */
suspend fun DiscordClient.modifyGuildMember(guildId: String, userId: String, payload: ModifyMemberPayload): HttpResponse {
    return httpClient.patch("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Remove (kick) a member from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 */
suspend fun DiscordClient.removeGuildMember(guildId: String, userId: String): HttpResponse {
    return httpClient.delete("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }
}
