import ktordiscord.components.Snowflake
import ktordiscord.components.snowflake

// Depuis une chaîne, via l'extension `.snowflake`
val channelId = "123456789012345678".snowflake

// Ou directement
val guildId = Snowflake("987654321098765432")
