package interactions

import DiscordClient
import DiscordEndpoints
import buildDiscordHeader
import builders.GlobalApplicationCommandBuilderScope
import components.Message
import components.enums.ApplicationCommandTypes
import components.enums.InteractionCallbackTypes
import components.interactions.ApplicationCommand
import components.interactions.InteractionCallBack
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun DiscordClient.createInteractionResponse(interactionId: String, interactionToken: String, interactionCallBackType: InteractionCallbackTypes, message: Message? = null): HttpResponse {
    val interactionCallBack = InteractionCallBack(interactionCallBackType, message)
    return httpClient.post("$discordURL/interactions/$interactionId/$interactionToken/callback") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(interactionCallBack)
    }
}

suspend fun DiscordClient.editOriginalInteractionResponse(applicationId: String, interactionToken: String, message: Message): HttpResponse {
    return httpClient.patch("$discordURL/webhooks/$applicationId/$interactionToken/messages/@original") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}

suspend fun DiscordClient.createGlobalApplicationCommand(name: String, type: ApplicationCommandTypes, init: GlobalApplicationCommandBuilderScope.() -> Unit): HttpResponse {
    val appId = this.applicationId
    val appCmd = GlobalApplicationCommandBuilderScope(name, type).apply(init).build()
    val response = httpClient.post("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$appId/${DiscordEndpoints.COMMANDS.text}") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(appCmd)
    }
    if (response.status.isSuccess()) {
        val newApp = response.body<ApplicationCommand>()
        this.interactionManager.appCommands[appCmd] = null
    }
    return response
}

suspend fun DiscordClient.getGlobalApplicationCommands(applicationId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId/${DiscordEndpoints.COMMANDS.text}") {
        buildDiscordHeader(token)
    }
}