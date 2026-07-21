import ktordiscord.components.snowflake

val userId = "555666777888999000".snowflake

// 1. Ouvrir (ou récupérer) le canal DM
val dm = kda.createDM(userId).getOrThrow()

// 2. Y envoyer un message comme dans n'importe quel salon
kda.sendMessage(dm.id) {
    content = "Salut en privé 👋"
}
