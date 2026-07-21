import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake

val guild = kda.getGuild(guildId).getOrNull()
println("Serveur : ${guild?.name}")
