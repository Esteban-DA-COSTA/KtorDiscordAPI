// handle { } : forme générale, avec accès complet à l'interaction et aux verbes.
kda.on("profil") {
    define { description = "Affiche ton profil" }
    handle {
        val userId = interaction.member?.user?.id
        respond {
            content = "Profil de <@$userId>"
            button("Rafraîchir") { style = ButtonStyle.SECONDARY }
                .click { update { content = "Profil rafraîchi." } }
        }
    }
}
