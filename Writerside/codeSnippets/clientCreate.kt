import ktordiscord.core.DiscordClient
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    // Configuration par défaut.
    val kda = DiscordClient.create(token = "myDiscordToken")

    // Bornes du cache de composants éphémères réglées explicitement.
    val tuned = DiscordClient.create(
        token = "myDiscordToken",
        componentCacheSize = 2048,
        componentCacheTtlMs = 30 * 60 * 1000L, // 30 min
    )
}
