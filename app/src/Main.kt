import builders.author
import builders.embed
import builders.field
import builders.iconUrl
import builders.name
import components.interactions.ApplicationCommandData
import gateway.events.MessageCreateEvent
import gateway.events.ReadyEvent
import interactions.createGlobalApplicationCommand
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
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
    discordClient.on("embed") {
        context(it) {
            differeMessage()
            delay(3000)
            val data = it.data as ApplicationCommandData
            val member = it.member
            val messageOption = data.options?.first { opt ->
                opt.name == "message"
            }
            editOriginalResponse {
                embed {
                    field {
                        name = "Embeded"
                        value = messageOption?.value ?: "Option not set"
                    }
                    member?.let { member ->
                        author {
                            member.user?.let { user ->
                                name(user.username ?: "Unknown")
                            }
                        }
                    }
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
