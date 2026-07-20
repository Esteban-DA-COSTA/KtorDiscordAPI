// Réagir à la connexion du bot.
kda.on<ReadyEvent> {
    println("Connecté !")
}

// Réagir à chaque message reçu. `event` est le MessageCreateEvent typé.
kda.on<MessageCreateEvent> {
    if (event.message.content == "ping") {
        reply { content = "pong!" }
    }
}
