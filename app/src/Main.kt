package ktordiscord.app

import ktordiscord.builders.embed
import ktordiscord.core.DiscordClient
import ktordiscord.core.createGlobalApplicationCommand
import ktordiscord.components.Snowflake
import ktordiscord.components.enums.InteractionTypes
import ktordiscord.components.interactions.ApplicationCommandData
import ktordiscord.components.interactions.Interaction
import ktordiscord.components.snowflake
import ktordiscord.gateway.events.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val discordClient = DiscordClient.create("secret")

    // Subscribe before login(): the flows are hot, so a collector started after login could miss
    // early events such as READY.
    launch {
        discordClient.events.collect { event ->
            when (event) {
                is ReadyEvent -> {
                    println("Logged in")
                }

                is MessageCreateEvent -> {
                    val messageContent = event.message.content
                    val messageOriginChannel = event.message.channelId!!
                    when (messageContent) {
                        "ping" -> discordClient.sendMessage(messageOriginChannel) {
                            content = "pong!"
                        }
                    }
                }

                else -> {
                    println("Received an event that I don't know how to handle: ${event::class.simpleName}")
                }
            }
        }
    }
    launch {
        discordClient.interactions.collect { interaction ->
            when (interaction.type) {
                InteractionTypes.APPLICATION_COMMAND -> {
                    handleEmbedInteractionCommand(interaction, discordClient)
                }

                else -> {
                    println("Received an interaction that I don't know how to handle: ${interaction.type}")
                }
            }
        }
    }

    discordClient.login(33283)

    val response = discordClient.createGlobalApplicationCommand("pingit") {
        description = "Send a embed ping message"
    }
    println(response)
    println(response.bodyAsText())
}

suspend fun handleEmbedInteractionCommand(interaction: Interaction, discordClient: DiscordClient) {
    val interactionData = interaction.data as ApplicationCommandData
    if (interaction.id == "1229445667831808122".snowflake) {
        val stringToEmbed = interactionData.options?.get(0)?.value as String
        discordClient.respondWithMessage(interaction) {
            embed {
                title = "Embeded message"
                description = stringToEmbed
            }
        }
    }
}