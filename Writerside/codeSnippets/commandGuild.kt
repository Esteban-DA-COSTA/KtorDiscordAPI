import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake

// Commande de serveur : propagation instantanée (idéal en développement).
kda.on("dev") {
    define(guildId) { description = "Commande de test" }
    respond { content = "OK" }
}
