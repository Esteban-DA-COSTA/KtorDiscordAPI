// Déclarer ET gérer la commande /ping.
kda.on("ping") {
    define { description = "Répond pong" }   // synchronisé sur login()
    respond { content = "pong" }
}

// … puis, plus loin :
kda.login(intents = 1)
