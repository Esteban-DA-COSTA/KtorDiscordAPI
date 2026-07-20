// `reply { }` envoie un nouveau message dans le salon d'origine de l'event.
// Il réutilise le même builder que `sendMessage` : on peut y attacher un bouton
// dont le callback de clic est câblé inline avec `.click { }`.
kda.on<MessageCreateEvent> {
    if (event.message.content == "menu") {
        reply {
            content = "Menu :"
            button("Rafraîchir", customId = "refresh") { style = ButtonStyle.SECONDARY }
                .click {
                    update { content = "Rafraîchi ! 🔄" }
                }
        }
    }
}
