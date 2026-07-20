kda.on("longtask") {
    // Accuse réception immédiatement : Discord affiche « réfléchit… ».
    defer()

    // Travail long (> 3 s) autorisé maintenant que l'interaction est acquittée.
    val result = doSlowWork()

    // Remplit le message de réponse initialement différé.
    editOriginal {
        content = "Terminé : $result"
    }
}
