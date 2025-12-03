import builders.embed
import components.Snowflake
import components.enums.InteractionTypes
import components.interactions.ApplicationCommandData
import components.interactions.Interaction
import components.snowflake
import gateway.events.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val discordClient = DiscordClient("secret")
        .also { it.login(33283) }
    launch {
        for (event in discordClient.events) {
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
        for (interaction in discordClient.interactions) {
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
                content = stringToEmbed
            }
        }
    }
}