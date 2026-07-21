package ktordiscord.core

import ktordiscord.components.RolePayload
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Create new role.
 *
 * @param guildId the guild id that corresponds to the guild to add the role.
 * @param rolePayload information of the role to create.
 * @return the created role in the body.
 */
suspend fun DiscordClient.createNewRole(guildId: String, rolePayload: RolePayload): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(rolePayload)
    }
}

/**
 * @param: guildID the guild id that corresponds to the guild to get
 * @return the guild whose id was passed as a parameter
 * */
suspend fun DiscordClient.getGuild(guildId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId") {
        buildDiscordHeader(token)
    }
}

/**
 * Get roles.
 *
 * Retrieve all roles of a guild
 * @param guildId the id of the guild.
 * @return An HttpResponse with a list of roles.
 */
suspend fun DiscordClient.getRoles(guildId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
    }
}
