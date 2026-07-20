package ktordiscord.app

import ktordiscord.builders.embed
import ktordiscord.components.enums.ButtonStyle
import ktordiscord.core.DiscordClient
import ktordiscord.core.InteractionKind
import ktordiscord.core.createGlobalApplicationCommand
import ktordiscord.gateway.events.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val discordClient = DiscordClient.create("secret")

    // Classic events. Subscribe before login(): the flows are hot, so a collector started after
    // login could miss early events such as READY.
    launch {
        discordClient.events.collect { event ->
            when (event) {
                is ReadyEvent -> println("Logged in")

                is MessageCreateEvent -> {
                    val messageContent = event.message.content
                    val messageOriginChannel = event.message.channelId!!
                    when (messageContent) {
                        "ping" -> discordClient.sendMessage(messageOriginChannel) {
                            content = "pong!"
                        }

                        // Un bouton sur un message classique (hors interaction). Son custom_id stable
                        // "refresh" est géré par le handler top-level enregistré plus bas.
                        "menu" -> discordClient.sendMessage(messageOriginChannel) {
                            content = "Menu :"
                            button("Rafraîchir", customId = "refresh") { style = ButtonStyle.SECONDARY }
                        }
                    }
                }

                else -> println("Received an event that I don't know how to handle: ${event::class.simpleName}")
            }
        }
    }

    // Interaction routing: one handler per command. A button added inside `respond { }` binds its
    // own click callback inline via `.click { }` — no separate top-level registration needed.
    discordClient.on("pingit") {
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

    discordClient.login(33283)

    val response = discordClient.createGlobalApplicationCommand("pingit") {
        description = "Send a ping message with a button"
    }
    println(response.status)
    println(response.bodyAsText())
}
