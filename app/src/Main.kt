package ktordiscord.app

import kotlinx.coroutines.runBlocking
import ktordiscord.components.enums.ButtonStyle
import ktordiscord.core.DiscordClient
import ktordiscord.core.InteractionKind
import ktordiscord.core.reply
import ktordiscord.gateway.events.MessageCreateEvent
import ktordiscord.gateway.events.ReadyEvent

fun main(): Unit = runBlocking {
    val discordClient = DiscordClient.create("secret")

    // Event routing: one handler per event type, registered by reified type. The dispatch loop
    // starts at construction, so registering before login() misses no early event (READY included).
    discordClient.on<ReadyEvent> { println("Logged in") }

    discordClient.on<MessageCreateEvent> {
        when (event.message.content) {
            "ping" -> reply { content = "pong!" }

            // Un bouton sur un message classique (hors interaction). Son custom_id stable
            // "refresh" est géré par le handler top-level enregistré plus bas.
            "menu" -> reply {
                content = "Menu :"
                button("Rafraîchir", customId = "refresh") { style = ButtonStyle.SECONDARY }
            }
        }
    }

    // Interaction routing: one `on` block both declares the command (`define`, synced to Discord on
    // login()) and registers its handler (`respond`). A button added inside `respond { }` binds its
    // own click callback inline via `.click { }` — no separate top-level registration needed.
    discordClient.on("pingit") {
        define { description = "Send a ping message with a button" }
        respond {
            content = "Pong ! Encore ?"
            embed {
                title = "Embeded message"
                description = "Réponse à la commande pingit"
            }
            button("Re-ping") { style = ButtonStyle.PRIMARY }
                .click {
                    update { content = "Re-pong ! 🏓" }
                }
        }
    }

    // Réagir à un composant par son custom_id, sans handler de commande autour (clé typée).
    discordClient.on(InteractionKind.Component, "refresh") {
        update { content = "Rafraîchi ! 🔄" }
    }

    // login() syncs every `define { }` above to Discord (bulk overwrite) and connects the Gateway.
    discordClient.login(33283)
}
