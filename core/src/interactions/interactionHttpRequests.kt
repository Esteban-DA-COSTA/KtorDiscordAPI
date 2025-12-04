package interactions

import DiscordClient
import buildDiscordHeader
import components.Message
import components.Snowflake
import components.enums.InteractionCallbackTypes
import components.interactions.ApplicationCommand
import components.interactions.InteractionCallBack
import components.snowflake
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

suspend fun DiscordClient.createInteractionResponse(interactionId: String, interactionToken: String, interactionCallBackType: InteractionCallbackTypes, message: Message? = null): HttpResponse {
    val interactionCallBack = InteractionCallBack(interactionCallBackType, message)
    return httpClient.post("$discordURL/interactions/$interactionId/$interactionToken/callback") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(interactionCallBack)
    }
}

suspend fun DiscordClient.createGlobalApplicationCommand(name: String, init: ApplicationCommand.() -> Unit): HttpResponse {
    val appId = this.applicationId
    val appCommand = ApplicationCommand(id = Snowflake("-1"), name = name, type = 0, applicationId = this.applicationId.snowflake, description = "").apply(init)
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