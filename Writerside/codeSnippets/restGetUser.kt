import ktordiscord.components.snowflake

val user = kda.getUser("555666777888999000".snowflake).getOrNull()
println(user?.username)
