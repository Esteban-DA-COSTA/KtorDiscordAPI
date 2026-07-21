import ktordiscord.components.CreateBanPayload
import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake
val userId = "555666777888999000".snowflake

// Bannir + supprimer les messages des dernières 24 h
kda.createGuildBan(guildId, userId, CreateBanPayload(deleteMessageSeconds = 24 * 3600))

// Lever le ban
kda.removeGuildBan(guildId, userId)
