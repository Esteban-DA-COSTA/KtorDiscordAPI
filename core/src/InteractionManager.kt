package ktordiscord.core

import ktordiscord.components.enums.InteractionTypes
import ktordiscord.components.interactions.ApplicationCommand

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
        val appCommands = client.getGlobalApplicationCommands(client.applicationId).getOrNull() ?: emptyList()
        this.appCommands[InteractionTypes.APPLICATION_COMMAND] = appCommands
    }
}
