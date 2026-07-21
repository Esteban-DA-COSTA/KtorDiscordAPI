package ktordiscord.core

import ktordiscord.components.interactions.ApplicationCommand
import ktordiscord.components.interactions.ApplicationCommandPayload
import ktordiscord.components.interactions.ApplicationCommandPermission
import ktordiscord.components.interactions.ApplicationCommandPermissionsPayload
import ktordiscord.components.interactions.GuildApplicationCommandPermissions
import io.ktor.client.request.*
import io.ktor.http.*

// Base path for the bot's own global commands: /applications/{appId}/commands
private val DiscordClient.globalCommandsUrl: String
    get() = "$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.COMMANDS.text}"

// Base path for the bot's own guild commands: /applications/{appId}/guilds/{guildId}/commands
private fun DiscordClient.guildCommandsUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.COMMANDS.text}"

//#region Global commands

suspend fun DiscordClient.createGlobalApplicationCommand(name: String, init: ApplicationCommandPayload.() -> Unit): DiscordResponse<ApplicationCommand> {
    val appCommand = ApplicationCommandPayload(name = name).apply(init)
    return httpClient.post(globalCommandsUrl) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }.decode()
}

suspend fun DiscordClient.getGlobalApplicationCommands(applicationId: String): DiscordResponse<List<ApplicationCommand>> {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.COMMANDS.text}") {
        buildDiscordHeader(token)
    }.decode()
}

/** Fetch a single global application command by its id. */
suspend fun DiscordClient.getGlobalApplicationCommand(commandId: String): DiscordResponse<ApplicationCommand> {
    return httpClient.get("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
    }.decode()
}

/** Edit an existing global application command. Only the fields set in [init] are sent. */
suspend fun DiscordClient.editGlobalApplicationCommand(commandId: String, init: ApplicationCommandPayload.() -> Unit): DiscordResponse<ApplicationCommand> {
    val appCommand = ApplicationCommandPayload().apply(init)
    return httpClient.patch("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }.decode()
}

/** Delete a global application command. */
suspend fun DiscordClient.deleteGlobalApplicationCommand(commandId: String): DiscordResponse<Unit> {
    return httpClient.delete("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

/**
 * Overwrite **all** global commands at once. Commands absent from [commands] are deleted — this is
 * the declarative bulk endpoint Discord recommends running on startup.
 */
suspend fun DiscordClient.bulkOverwriteGlobalApplicationCommands(commands: List<ApplicationCommandPayload>): DiscordResponse<List<ApplicationCommand>> {
    return httpClient.put(globalCommandsUrl) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(commands)
    }.decode()
}

//#endregion

//#region Guild commands

suspend fun DiscordClient.createGuildApplicationCommand(guildId: String, name: String, init: ApplicationCommandPayload.() -> Unit): DiscordResponse<ApplicationCommand> {
    val appCommand = ApplicationCommandPayload(name = name).apply(init)
    return httpClient.post(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }.decode()
}

suspend fun DiscordClient.getGuildApplicationCommands(guildId: String): DiscordResponse<List<ApplicationCommand>> {
    return httpClient.get(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
    }.decode()
}

/** Fetch a single guild application command by its id. */
suspend fun DiscordClient.getGuildApplicationCommand(guildId: String, commandId: String): DiscordResponse<ApplicationCommand> {
    return httpClient.get("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
    }.decode()
}

/** Edit an existing guild application command. Only the fields set in [init] are sent. */
suspend fun DiscordClient.editGuildApplicationCommand(guildId: String, commandId: String, init: ApplicationCommandPayload.() -> Unit): DiscordResponse<ApplicationCommand> {
    val appCommand = ApplicationCommandPayload().apply(init)
    return httpClient.patch("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }.decode()
}

/** Delete a guild application command. */
suspend fun DiscordClient.deleteGuildApplicationCommand(guildId: String, commandId: String): DiscordResponse<Unit> {
    return httpClient.delete("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
    }.decodeEmpty()
}

/**
 * Overwrite **all** commands of a guild at once. Commands absent from [commands] are deleted.
 * Guild commands propagate instantly, which makes this the go-to for local development.
 */
suspend fun DiscordClient.bulkOverwriteGuildApplicationCommands(guildId: String, commands: List<ApplicationCommandPayload>): DiscordResponse<List<ApplicationCommand>> {
    return httpClient.put(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(commands)
    }.decode()
}

//#endregion

//#region Command permissions

/** Fetch the permissions for **all** commands of the bot in a guild. */
suspend fun DiscordClient.getGuildApplicationCommandPermissions(guildId: String): DiscordResponse<List<GuildApplicationCommandPermissions>> {
    return httpClient.get("${guildCommandsUrl(guildId)}/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
    }.decode()
}

/** Fetch the permissions for a single command in a guild. */
suspend fun DiscordClient.getApplicationCommandPermissions(guildId: String, commandId: String): DiscordResponse<GuildApplicationCommandPermissions> {
    return httpClient.get("${guildCommandsUrl(guildId)}/$commandId/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
    }.decode()
}

/**
 * Overwrite the permissions for a command in a guild.
 *
 * Note: this endpoint requires a **bearer token** with the `applications.commands.permissions.update`
 * scope, not the bot token — see the Discord docs.
 */
suspend fun DiscordClient.editApplicationCommandPermissions(guildId: String, commandId: String, permissions: List<ApplicationCommandPermission>): DiscordResponse<GuildApplicationCommandPermissions> {
    return httpClient.put("${guildCommandsUrl(guildId)}/$commandId/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(ApplicationCommandPermissionsPayload(permissions))
    }.decode()
}

//#endregion
