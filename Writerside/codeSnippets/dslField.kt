import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    embed {
        title = "Statistiques"
        field {
            name = "Serveurs"
            value = "42"
            inline = true
        }
        field {
            name = "Utilisateurs"
            value = "1337"
            inline = true
        }
    }
}
