import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    content = "Choisis :"
    button("Valider") { style = ButtonStyle.SUCCESS }
    button("Annuler") { style = ButtonStyle.DANGER }
    button("Docs") {
        style = ButtonStyle.LINK
        url = "https://example.com/docs"
    }
}
