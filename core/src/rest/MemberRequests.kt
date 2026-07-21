package ktordiscord.core

import ktordiscord.components.AddMemberPayload
import ktordiscord.components.DiscordError
import ktordiscord.components.Member
import ktordiscord.components.ModifyMemberPayload
import ktordiscord.components.Snowflake
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

private fun DiscordClient.membersUrl(guildId: Snowflake): String =
    "$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.MEMBERS.text}"

/**
 * List members of a guild.
 *
 * @param guildId the id of the guild.
 * @param limit max number of members to return (1–1000).
 * @param after get members after this user id (pagination).
 * @return a [DiscordResponse] wrapping a list of members.
 */
suspend fun DiscordClient.listGuildMembers(guildId: Snowflake, limit: Int? = null, after: Snowflake? = null): DiscordResponse<List<Member>> {
    return httpClient.get(membersUrl(guildId)) {
        buildDiscordHeader(token)
        limit?.let { parameter("limit", it) }
        after?.let { parameter("after", it) }
    }.decode()
}

/**
 * Get a single guild member.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 * @return a [DiscordResponse] wrapping the member.
 */
suspend fun DiscordClient.getGuildMember(guildId: Snowflake, userId: Snowflake): DiscordResponse<Member> {
    return httpClient.get("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }.decode()
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
 * @return a [DiscordResponse] wrapping the added member, or `null` on `204` (the user was already a member).
 */
suspend fun DiscordClient.addGuildMember(guildId: Snowflake, userId: Snowflake, payload: AddMemberPayload): DiscordResponse<Member?> {
    val response = httpClient.put("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
    return when {
        response.status == HttpStatusCode.NoContent -> DiscordResponse.Success(null, response.status)
        response.status.isSuccess() -> DiscordResponse.Success(response.body<Member>(), response.status)
        else -> DiscordResponse.Failure(response.status, runCatching { response.body<DiscordError>() }.getOrNull())
    }
}

/**
 * Modify attributes of a guild member.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 * @param payload the fields to update.
 * @return a [DiscordResponse] wrapping the updated member.
 */
suspend fun DiscordClient.modifyGuildMember(guildId: Snowflake, userId: Snowflake, payload: ModifyMemberPayload): DiscordResponse<Member> {
    return httpClient.patch("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

/**
 * Remove (kick) a member from a guild.
 *
 * @param guildId the id of the guild.
 * @param userId the id of the member's user.
 */
suspend fun DiscordClient.removeGuildMember(guildId: Snowflake, userId: Snowflake): DiscordResponse<Unit> {
    return httpClient.delete("${membersUrl(guildId)}/$userId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}
