kda.on("compteur") {
    respond {
        content = "Compteur : 0"
        button("+1") { style = ButtonStyle.PRIMARY }
            .click {
                // Modifie le message porteur au lieu d'en envoyer un nouveau.
                update { content = "Compteur : 1" }
            }
    }
}
