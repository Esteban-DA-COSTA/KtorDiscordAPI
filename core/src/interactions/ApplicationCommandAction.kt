package interactions

import DiscordClient
import components.Message
import components.enums.InteractionCallbackTypes
import components.interactions.ApplicationCommand
import components.interactions.Interaction
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class ApplicationCommandAction(
    val applicationCommand: ApplicationCommand,
    val client: DiscordClient,
    var action: suspend ApplicationCommandAction.(Interaction) -> Unit = {}
) {

    suspend inline fun executeAction(interaction: Interaction) = action(interaction)


    context(interaction: Interaction)
    suspend fun differeMessage() {
        client.createInteractionResponse(
            interaction.id.value,
            interaction.token,
            InteractionCallbackTypes.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE
        )
    }

    /**
     * Sends a response message to a Discord interaction. This method creates an interaction response
     * of type `CHANNEL_MESSAGE_WITH_SOURCE` and uses the passed `Message` configuration.
     *
     * @param interaction The interaction object representing the Discord interaction to respond to.
     * @param init A lambda function used to configure the message to be sent as a response.
     */
    context(interaction: Interaction)
    suspend fun respondWithMessage( init: (Message.() -> Unit)) {

        client.createInteractionResponse(
            interaction.id.value,
            interaction.token,
            InteractionCallbackTypes.CHANNEL_MESSAGE_WITH_SOURCE,
            Message().apply(init)
        )
    }

    context(interaction: Interaction)
    suspend fun editOriginalResponse(init: (Message.() -> Unit)) {
        val response =  client.editOriginalInteractionResponse(
            client.applicationId,
            interaction.token,
            Message().apply(init)
        )
        if (!response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            client.httpClientLogger.error { "Failed to edit original interaction response: $responseBody" }
        }
    }
}