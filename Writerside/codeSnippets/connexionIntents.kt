// Les intents sont un bitfield : on combine les bits voulus avec `or`.
val GUILDS = 1 shl 0          // 1
val GUILD_MESSAGES = 1 shl 9  // 512
val MESSAGE_CONTENT = 1 shl 15 // 32768

kda.login(intents = GUILDS or GUILD_MESSAGES or MESSAGE_CONTENT)
