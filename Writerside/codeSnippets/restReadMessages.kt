import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

// Un message précis
val message = kda.getChannelMessage(channelId, "111222333444555666".snowflake).getOrNull()

// Les 50 derniers messages
val history = kda.getChannelMessages(channelId, limit = 50).getOrNull()
