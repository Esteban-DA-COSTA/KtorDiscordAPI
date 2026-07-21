import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    content = "Hello, world!"
}.onFailure { println("échec ${it.status}") }
