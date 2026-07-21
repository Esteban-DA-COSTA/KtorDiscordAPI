package ktordiscord.core

import ktordiscord.components.Message
import ktordiscord.components.MessagePayload
import ktordiscord.components.enums.InteractionCallbackTypes
import ktordiscord.components.interactions.InteractionCallBack
import io.ktor.client.request.*
import io.ktor.http.*

suspend fun DiscordClient.createInteractionResponse(interactionId: String, interactionToken: String, interactionCallBackType: InteractionCallbackTypes, message: MessagePayload? = null): DiscordResponse<Unit> {
    val interactionCallBack = InteractionCallBack(interactionCallBackType, message)
    return httpClient.post("$discordURL/interactions/$interactionId/$interactionToken/callback") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(interactionCallBack)
    }.decodeEmpty()
}

/**
 * Edit the initial ("@original") response of an interaction — used after a deferred response to fill
 * in the actual message. Targets the interaction webhook, so it takes the application id and the
 * interaction token (not the interaction id).
 *
 * @return a [DiscordResponse] wrapping the edited message.
 */
suspend fun DiscordClient.editOriginalInteractionResponse(applicationId: String, interactionToken: String, message: MessagePayload): DiscordResponse<Message> {
    return httpClient.patch("$discordURL/webhooks/$applicationId/$interactionToken/messages/@original") {
        buildDiscordHeader(token)
        contentType(ContentType.Application.Json)
        setBody(message)
    }.decode()
}
