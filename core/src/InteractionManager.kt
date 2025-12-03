import components.enums.InteractionTypes
import components.interactions.ApplicationCommand
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking

class InteractionManager(private val client: DiscordClient) {

    init {
        runBlocking {
            retrieveInteractionCommands()
        }
    }

    private val appCommands: MutableMap<InteractionTypes, List<ApplicationCommand>> = mutableMapOf()

    private suspend fun retrieveInteractionCommands() {
        val httpResponse = client.getGlobalApplicationCommands(client.applicationId)
        val appCommands = httpResponse.body<List<ApplicationCommand>>()
        this.appCommands[InteractionTypes.APPLICATION_COMMAND] = appCommands
    }
}