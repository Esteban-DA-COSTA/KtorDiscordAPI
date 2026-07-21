import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    content = "Un message texte simple."
    tts = false
}
