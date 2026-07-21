// Récupérer l'objet, ou null en cas d'échec
val message = kda.sendMessage(channelId) { content = "Hello!" }.getOrNull()

// Réagir uniquement au succès et/ou à l'échec (chaînable)
kda.sendMessage(channelId) { content = "Hello!" }
    .onSuccess { println("id = ${it.id}") }
    .onFailure { println("échec ${it.status} : ${it.error?.message}") }

// Récupérer l'objet, ou lever une DiscordApiException en cas d'échec
val created = kda.sendMessage(channelId) { content = "Hello!" }.getOrThrow()
