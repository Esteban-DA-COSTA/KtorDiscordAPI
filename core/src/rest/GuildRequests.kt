package ktordiscord.core

import ktordiscord.components.CreateChannelPayload
import ktordiscord.components.RolePayload
import ktordiscord.components.RolePositionPayload
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

//#region Channels

/**
 * Get all channels of a guild.
 *
 * @param guildId the id of the guild.
 * @return an [HttpResponse] whose body is a list of channels.
 */
suspend fun DiscordClient.getGuildChannels(guildId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
    }
}

/**
 * Create a channel in a guild.
 *
 * @param guildId the id of the guild.
 * @param payload the channel to create ([CreateChannelPayload.name] is required).
 * @return an [HttpResponse] whose body is the created channel.
 */
suspend fun DiscordClient.createGuildChannel(guildId: String, payload: CreateChannelPayload): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

//#endregion

//#region Roles

/**
 * Edit an existing role.
 *
 * @param guildId the id of the guild.
 * @param roleId the id of the role to edit.
 * @param payload the fields to update.
 * @return an [HttpResponse] whose body is the updated role.
 */
suspend fun DiscordClient.editRole(guildId: String, roleId: String, payload: RolePayload): HttpResponse {
    return httpClient.patch("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}/$roleId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }
}

/**
 * Delete a role.
 *
 * @param guildId the id of the guild.
 * @param roleId the id of the role to delete.
 */
suspend fun DiscordClient.deleteRole(guildId: String, roleId: String): HttpResponse {
    return httpClient.delete("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}/$roleId") {
        buildDiscordHeader(token)
    }
}

/**
 * Modify the positions of a set of roles in a guild.
 *
 * @param guildId the id of the guild.
 * @param positions the new positions (id + position) to apply.
 * @return an [HttpResponse] whose body is the full list of guild roles.
 */
suspend fun DiscordClient.modifyRolePositions(guildId: String, positions: List<RolePositionPayload>): HttpResponse {
    return httpClient.patch("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(positions)
    }
}

//#endregion
