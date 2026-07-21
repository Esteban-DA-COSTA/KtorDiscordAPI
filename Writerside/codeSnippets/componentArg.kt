import ktordiscord.core.InteractionKind

// Le bouton encode un état après le séparateur ':'
kda.on("mod") {
    respond {
        content = "Approuver la demande #42 ?"
        button("Approuver", customId = "approve:42") { style = ButtonStyle.SUCCESS }
    }
}

// Un seul handler "approve" traite approve:42, approve:99… ; l'état est dans `arg`.
kda.on(InteractionKind.Component, "approve") {
    update { content = "Demande #$arg approuvée ✅" }
}
