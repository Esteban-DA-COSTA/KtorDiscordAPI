package ktordiscord.app

import kotlinx.coroutines.runBlocking
import ktordiscord.components.enums.ApplicationCommandTypes
import ktordiscord.components.enums.ButtonStyle
import ktordiscord.components.enums.StatusTypeEnum
import ktordiscord.core.DiscordClient
import ktordiscord.core.InteractionKind
import ktordiscord.core.onFailure
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
            // Les verbes REST renvoient désormais un DiscordResponse<T> : on peut ignorer le
            // résultat, ou réagir à l'échec (statut HTTP + corps d'erreur Discord typé).
            "ping" -> reply {
                content = "pong!"
            }.onFailure { println("Échec d'envoi (${it.status}) : ${it.error?.message}") }

            // Un bouton sur un message classique (hors interaction). Son custom_id stable
            // "refresh" est géré par le handler top-level enregistré plus bas.
            "menu" -> reply {
                content = "Menu :"
                button("Rafraîchir", customId = "refresh") { style = ButtonStyle.SECONDARY }
            }

            "killMe" -> reply {
                content = "Ok je meurs"
            }.also {
                discordClient.close()
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
            button("Re-ping") { style = ButtonStyle.PRIMARY }.click {
                    update { content = "Re-pong ! 🏓" }
                }
        }
    }

    discordClient.on("killme") {
        define {
            description = "Kill the bot"
            type = ApplicationCommandTypes.CHAT_INPUT
        }
        respond {
            ephemeral()
            embed {
                title = "Are you sure ?"
                description = "Are you sure you want to kill me ?"
            }
            button("Yes", "yes") {
                style = ButtonStyle.PRIMARY
            }.click {
                respond { content = "Bye bye !" }
                discordClient.updatePresence { 
                    status = StatusTypeEnum.OFFLINE
                }
                discordClient.close()
            }
            button("No", "no") {
                style = ButtonStyle.DANGER
            }.click {
                respond { content = "Ok, I won't kill myself !" }
            }
        }
    }

    // Réagir à un composant par son custom_id, sans handler de commande autour (clé typée).
    discordClient.on(InteractionKind.Component, "refresh") {
        update { content = "Rafraîchi ! 🔄" }
    }

    // login() syncs every `define { }` above to Discord (bulk overwrite) and connects the Gateway,
    // returning the Job of the (reconnecting) Gateway loop. join() keeps runBlocking — and the process
    // — alive for as long as the bot is connected; without it main returns immediately and the JVM
    // exits (the client's coroutines run on daemon threads).
    discordClient.login(33283).join()
}
