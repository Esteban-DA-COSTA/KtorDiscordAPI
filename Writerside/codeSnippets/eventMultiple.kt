// Plusieurs handlers peuvent être enregistrés pour le même type d'event.
// Ils s'exécutent tous (dans des coroutines indépendantes).
kda.on<MessageCreateEvent> { logIncomingMessage(event.message) }
kda.on<MessageCreateEvent> { runModeration(event.message) }
