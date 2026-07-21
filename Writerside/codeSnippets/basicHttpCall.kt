val channelId = "myDiscordChannelId"

// Chaque appel REST renvoie un DiscordResponse<T>.
val response = kda.sendMessage(channelId) {
    content = "Hello, world!"
}

when (response) {
    is DiscordResponse.Success -> println("Message envoyé (id = ${response.value.id})")
    is DiscordResponse.Failure -> println("Erreur ${response.status} : ${response.error?.message}")
}
