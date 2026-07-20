kda.on("pingit") {
    respond {
        content = "Pong ! Encore ?"

        button("Re-ping") { style = ButtonStyle.PRIMARY }
            .click {
                // Callback exécuté au clic sur le bouton.
                update { content = "Re-pong ! 🏓" }
            }
    }
}
