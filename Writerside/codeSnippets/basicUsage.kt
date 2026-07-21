import ktordiscord.core.DiscordClient
import ktordiscord.gateway.events.MessageCreateEvent
import ktordiscord.gateway.events.ReadyEvent
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val kda = DiscordClient.create(token = "myDiscordToken")

    // Handlers enregistrés AVANT login().
    kda.on<ReadyEvent> { println("Connecté !") }

    kda.on<MessageCreateEvent> {
        if (event.message.content == "ping") {
            reply { content = "pong!" }
        }
    }

    // Intents : GUILDS | GUILD_MESSAGES | MESSAGE_CONTENT
    kda.login(intents = 1 or 512 or 32768)
}
