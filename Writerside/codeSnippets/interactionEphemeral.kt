kda.on("secret") {
    respond {
        ephemeral() // Seul l'utilisateur qui a lancé la commande voit ce message.
        content = "Ceci n'est visible que par toi."
    }
}
