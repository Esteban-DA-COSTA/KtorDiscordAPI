import components.interactions.ApplicationCommand
import components.interactions.Interaction

class ApplicationCommandAction(
    val applicationCommand: ApplicationCommand,
    val client: DiscordClient,
    var action: suspend ApplicationCommandAction.(Interaction) -> Unit = {}
) {

    suspend fun executeAction(interaction: Interaction) = action(interaction)


    fun ack() {
        TODO()
    }

    fun respondWithMessage() {
        TODO()
    }
}