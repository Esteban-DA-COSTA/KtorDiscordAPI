package ktordiscord.core

import ktordiscord.components.Channel
import ktordiscord.components.CreateChannelPayload
import ktordiscord.components.Guild
import ktordiscord.components.Role
import ktordiscord.components.RolePayload
import ktordiscord.components.RolePositionPayload
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Create new role.
 *
 * @param guildId the guild id that corresponds to the guild to add the role.
 * @param rolePayload information of the role to create.
 * @return a [DiscordResponse] wrapping the created role.
 */
suspend fun DiscordClient.createNewRole(guildId: String, rolePayload: RolePayload): DiscordResponse<Role> {
    return httpClient.post("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(rolePayload)
    }.decode()
}

/**
 * @param: guildID the guild id that corresponds to the guild to get
 * @return a [DiscordResponse] wrapping the guild whose id was passed as a parameter
 * */
suspend fun DiscordClient.getGuild(guildId: String): DiscordResponse<Guild> {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Get roles.
 *
 * Retrieve all roles of a guild
 * @param guildId the id of the guild.
 * @return a [DiscordResponse] wrapping a list of roles.
 */
suspend fun DiscordClient.getRoles(guildId: String): DiscordResponse<List<Role>> {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
    }.decode()
}

//#region Channels

/**
 * Get all channels of a guild.
 *
 * @param guildId the id of the guild.
 * @return a [DiscordResponse] wrapping a list of channels.
 */
suspend fun DiscordClient.getGuildChannels(guildId: String): DiscordResponse<List<Channel>> {
    return httpClient.get("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Create a channel in a guild.
 *
 * @param guildId the id of the guild.
 * @param payload the channel to create ([CreateChannelPayload.name] is required).
 * @return a [DiscordResponse] wrapping the created channel.
 */
suspend fun DiscordClient.createGuildChannel(guildId: String, payload: CreateChannelPayload): DiscordResponse<Channel> {
    return httpClient.post("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.CHANNELS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

//#endregion

//#region Roles

/**
 * Edit an existing role.
 *
 * @param guildId the id of the guild.
 * @param roleId the id of the role to edit.
 * @param payload the fields to update.
 * @return a [DiscordResponse] wrapping the updated role.
 */
suspend fun DiscordClient.editRole(guildId: String, roleId: String, payload: RolePayload): DiscordResponse<Role> {
    return httpClient.patch("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}/$roleId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(payload)
    }.decode()
}

/**
 * Delete a role.
 *
 * @param guildId the id of the guild.
 * @param roleId the id of the role to delete.
 */
suspend fun DiscordClient.deleteRole(guildId: String, roleId: String): DiscordResponse<Unit> {
    return httpClient.delete("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}/$roleId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

/**
 * Modify the positions of a set of roles in a guild.
 *
 * @param guildId the id of the guild.
 * @param positions the new positions (id + position) to apply.
 * @return a [DiscordResponse] wrapping the full list of guild roles.
 */
suspend fun DiscordClient.modifyRolePositions(guildId: String, positions: List<RolePositionPayload>): DiscordResponse<List<Role>> {
    return httpClient.patch("$discordURL/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.ROLES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(positions)
    }.decode()
}

//#endregion
