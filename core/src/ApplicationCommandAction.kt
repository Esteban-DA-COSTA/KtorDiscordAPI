import components.interactions.ApplicationCommand
import components.interactions.Interaction

class ApplicationCommandAction(
    val applicationCommand: ApplicationCommand,
    val client: DiscordClient,
    var action: suspend (Interaction) -> Unit = {}
) {


    fun differe() {
        client.respondWithMessage()
    }
}