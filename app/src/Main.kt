import builders.author
import builders.embed
import builders.field
import builders.name
import components.enums.ApplicationCommandTypes
import components.enums.InteractionContextTypes
import components.interactions.ApplicationCommandData
import gateway.events.MessageCreateEvent
import gateway.events.ReadyEvent
import interactions.createGlobalApplicationCommand
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
    createAppCommand(discordClient)
    handleAppCommandInteractions(discordClient)
}

suspend fun createAppCommand(discordClient: DiscordClient) {
    discordClient.createGlobalApplicationCommand("whoiam", ApplicationCommandTypes.CHAT_INPUT) {
        description = "Shows who you are"
    }
    println("Finish creating app command")
}

fun handleAppCommandInteractions(discordClient: DiscordClient) {
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
    discordClient.on("whoiam") {
        context(it) {
            differeMessage()

            val (user, member) = when (it.context) {
                InteractionContextTypes.GUILD -> Pair(it.member!!.user!!, it.member!!)
                else -> Pair(it.user!!, null)
            }
            editOriginalResponse {
                embed {
                    field {
                        name = "User"
                        value = user.username ?: "Unknown"
                    }
                    member?.let { member ->
                        field {
                            name = "Member"
                            value = member.nick ?: "No nickname"
                        }
                    }
                }
            }
        }
    }
}
