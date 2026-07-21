import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

val channel = kda.getChannel(channelId).getOrNull()
println("Salon : ${channel?.name}")
