package ktordiscord.core

import ktordiscord.components.MessagePayload
import ktordiscord.components.RolePayload
import ktordiscord.components.Snowflake
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.ApplicationCommandPayload
import ktordiscord.components.interactions.ApplicationCommandPermission
import ktordiscord.components.interactions.ApplicationCommandPermissionsPayload
import ktordiscord.components.interactions.InteractionCallBack
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

suspend fun DiscordClient.getApplication(applicationId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId") {
        buildDiscordHeader(token)
    }
}

suspend fun DiscordClient.getMeApplicationId(): Snowflake {
    val response = httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/@me") {
        buildDiscordHeader(token)
    }
    val jsonBody = response.body<JsonElement>()
    val primitive = jsonBody.jsonObject["id"] as JsonPrimitive
    return Snowflake(primitive.content)
}

private suspend fun DiscordClient.getGateway(): HttpResponse {
    return httpClient.get("$discordURL/gateway") {
        buildDiscordHeader(token)
    }
}

suspend fun DiscordClient.createChannelMessage(channelId: String, message: MessagePayload): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

suspend fun DiscordClient.createInteractionResponse(interactionId: String, interactionToken: String, interactionCallBackType: InteractionCallbackTypes, message: MessagePayload? = null): HttpResponse {
    val interactionCallBack = InteractionCallBack(interactionCallBackType, message)
    return httpClient.post("$discordURL/interactions/$interactionId/$interactionToken/callback") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(interactionCallBack)
    }
}

/**
 * Edit the initial ("@original") response of an interaction — used after a deferred response to fill
 * in the actual message. Targets the interaction webhook, so it takes the application id and the
 * interaction token (not the interaction id).
 */
suspend fun DiscordClient.editOriginalInteractionResponse(applicationId: String, interactionToken: String, message: MessagePayload): HttpResponse {
    return httpClient.patch("$discordURL/webhooks/$applicationId/$interactionToken/messages/@original") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

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

//#region Application Command endpoints

// Base path for the bot's own global commands: /applications/{appId}/commands
private val DiscordClient.globalCommandsUrl: String
    get() = "$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.COMMANDS.text}"

// Base path for the bot's own guild commands: /applications/{appId}/guilds/{guildId}/commands
private fun DiscordClient.guildCommandsUrl(guildId: String): String =
    "$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.GUILDS.text}/$guildId/${DiscordEndpoints.COMMANDS.text}"

//#region Global commands

suspend fun DiscordClient.createGlobalApplicationCommand(name: String, init: ApplicationCommandPayload.() -> Unit): HttpResponse {
    val appCommand = ApplicationCommandPayload(name = name).apply(init)
    return httpClient.post(globalCommandsUrl) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }
}

suspend fun DiscordClient.getGlobalApplicationCommands(applicationId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.COMMANDS.text}") {
        buildDiscordHeader(token)
    }
}

/** Fetch a single global application command by its id. */
suspend fun DiscordClient.getGlobalApplicationCommand(commandId: String): HttpResponse {
    return httpClient.get("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
    }
}

/** Edit an existing global application command. Only the fields set in [init] are sent. */
suspend fun DiscordClient.editGlobalApplicationCommand(commandId: String, init: ApplicationCommandPayload.() -> Unit): HttpResponse {
    val appCommand = ApplicationCommandPayload().apply(init)
    return httpClient.patch("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }
}

/** Delete a global application command. */
suspend fun DiscordClient.deleteGlobalApplicationCommand(commandId: String): HttpResponse {
    return httpClient.delete("$globalCommandsUrl/$commandId") {
        buildDiscordHeader(token)
    }
}

/**
 * Overwrite **all** global commands at once. Commands absent from [commands] are deleted — this is
 * the declarative bulk endpoint Discord recommends running on startup.
 */
suspend fun DiscordClient.bulkOverwriteGlobalApplicationCommands(commands: List<ApplicationCommandPayload>): HttpResponse {
    return httpClient.put(globalCommandsUrl) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(commands)
    }
}

//#endregion

//#region Guild commands

suspend fun DiscordClient.createGuildApplicationCommand(guildId: String, name: String, init: ApplicationCommandPayload.() -> Unit): HttpResponse {
    val appCommand = ApplicationCommandPayload(name = name).apply(init)
    return httpClient.post(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }
}

suspend fun DiscordClient.getGuildApplicationCommands(guildId: String): HttpResponse {
    return httpClient.get(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
    }
}

/** Fetch a single guild application command by its id. */
suspend fun DiscordClient.getGuildApplicationCommand(guildId: String, commandId: String): HttpResponse {
    return httpClient.get("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
    }
}

/** Edit an existing guild application command. Only the fields set in [init] are sent. */
suspend fun DiscordClient.editGuildApplicationCommand(guildId: String, commandId: String, init: ApplicationCommandPayload.() -> Unit): HttpResponse {
    val appCommand = ApplicationCommandPayload().apply(init)
    return httpClient.patch("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCommand)
    }
}

/** Delete a guild application command. */
suspend fun DiscordClient.deleteGuildApplicationCommand(guildId: String, commandId: String): HttpResponse {
    return httpClient.delete("${guildCommandsUrl(guildId)}/$commandId") {
        buildDiscordHeader(token)
    }
}

/**
 * Overwrite **all** commands of a guild at once. Commands absent from [commands] are deleted.
 * Guild commands propagate instantly, which makes this the go-to for local development.
 */
suspend fun DiscordClient.bulkOverwriteGuildApplicationCommands(guildId: String, commands: List<ApplicationCommandPayload>): HttpResponse {
    return httpClient.put(guildCommandsUrl(guildId)) {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(commands)
    }
}

//#endregion

//#region Command permissions

/** Fetch the permissions for **all** commands of the bot in a guild. */
suspend fun DiscordClient.getGuildApplicationCommandPermissions(guildId: String): HttpResponse {
    return httpClient.get("${guildCommandsUrl(guildId)}/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
    }
}

/** Fetch the permissions for a single command in a guild. */
suspend fun DiscordClient.getApplicationCommandPermissions(guildId: String, commandId: String): HttpResponse {
    return httpClient.get("${guildCommandsUrl(guildId)}/$commandId/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
    }
}

/**
 * Overwrite the permissions for a command in a guild.
 *
 * Note: this endpoint requires a **bearer token** with the `applications.commands.permissions.update`
 * scope, not the bot token — see the Discord docs.
 */
suspend fun DiscordClient.editApplicationCommandPermissions(guildId: String, commandId: String, permissions: List<ApplicationCommandPermission>): HttpResponse {
    return httpClient.put("${guildCommandsUrl(guildId)}/$commandId/${DiscordEndpoints.PERMISSIONS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(ApplicationCommandPermissionsPayload(permissions))
    }
}

//#endregion

//#endregion