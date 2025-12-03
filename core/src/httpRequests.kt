import components.Message
import components.RolePayload
import components.Snowflake
import components.enums.InteractionCallbackTypes
import components.interactions.ApplicationCommand
import components.interactions.ApplicationCommandData
import components.interactions.InteractionCallBack
import components.snowflake
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

suspend fun DiscordClient.createChannelMessage(channelId: String, message: Message): HttpResponse {
    return httpClient.post("$discordURL/${DiscordEndpoints.CHANNELS.text}/$channelId/${DiscordEndpoints.MESSAGES.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

suspend fun DiscordClient.createInteractionResponse(interactionId: String, interactionToken: String, interactionCallBackType: InteractionCallbackTypes, message: Message): HttpResponse {
    val interactionCallBack = InteractionCallBack(interactionCallBackType, message)
    return httpClient.post("$discordURL/interactions/$interactionId/$interactionToken/callback") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(interactionCallBack)
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

suspend fun DiscordClient.createGlobalApplicationCommand(name: String, init: ApplicationCommand.() -> Unit): HttpResponse {
    val appId = this.applicationId
    val appCommand = ApplicationCommand(id = Snowflake("-1"), name = name, type = 0, description = "").apply(init)
    return httpClient.post("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$appId/${DiscordEndpoints.COMMANDS.text}") {
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