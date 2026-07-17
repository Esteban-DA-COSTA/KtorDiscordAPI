import components.enums.InteractionTypes
import components.interactions.ApplicationCommand
import io.ktor.client.call.*

class InteractionManager private constructor(private val client: DiscordClient) {

    private val appCommands: MutableMap<InteractionTypes, List<ApplicationCommand>> = mutableMapOf()

    companion object {
        /**
         * Create an [InteractionManager] and load the application's global commands.
         *
         * Suspends on a REST call, so it cannot happen in a constructor/`init` block.
         */
        suspend fun create(client: DiscordClient): InteractionManager {
            val manager = InteractionManager(client)
            manager.retrieveInteractionCommands()
            return manager
        }
    }

    private suspend fun retrieveInteractionCommands() {
        val httpResponse = client.getGlobalApplicationCommands(client.applicationId)
        val appCommands = httpResponse.body<List<ApplicationCommand>>()
        this.appCommands[InteractionTypes.APPLICATION_COMMAND] = appCommands
    }
}
