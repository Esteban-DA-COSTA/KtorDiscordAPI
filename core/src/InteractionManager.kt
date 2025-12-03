import components.Snowflake
import components.enums.InteractionTypes
import components.interactions.ApplicationCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking

class InteractionManager(private val client: DiscordClient) {
    val logger = KotlinLogging.logger(InteractionManager::class.simpleName ?: "InteractionManager")

    val appCommands: MutableMap<ApplicationCommand, ApplicationCommandAction?> = mutableMapOf()

    init {
        runBlocking {
            retrieveInteractionCommands()
        }
    }

    /**
     * Request discord to retrieve already defined interaction commands.
     */
    private suspend fun retrieveInteractionCommands() {
        val httpResponse = client.getGlobalApplicationCommands(client.applicationId)
        val appCommands = httpResponse.body<List<ApplicationCommand>>()
        appCommands.forEach { command -> this.appCommands[command] = null }
    }
}