import ktordiscord.core.DiscordClient
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val token = "myDiscordToken"
    val kda = DiscordClient.create(token)
}
