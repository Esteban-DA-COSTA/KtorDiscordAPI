import ktordiscord.components.snowflake
import ktordiscord.core.InteractionKind

val channelId = "123456789012345678".snowflake

// Un bouton sur un message classique (hors interaction), avec un custom_id stable.
kda.sendMessage(channelId) {
    content = "Menu :"
    button("Rafraîchir", customId = "refresh") { style = ButtonStyle.SECONDARY }
}

// Le handler du composant, enregistré au niveau du client — aucune commande autour.
kda.on(InteractionKind.Component, "refresh") {
    update { content = "Rafraîchi ! 🔄" }
}
