import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    embed {
        title = "Titre de l'embed"
        description = "Une description un peu plus longue."
        url = "https://example.com"

        author { name = "KDA" }
        footer { text = "Pied de page" }
        thumbnail { url = "https://example.com/thumb.png" }
    }
}
