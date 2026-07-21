import ktordiscord.components.ModifyMemberPayload
import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake
val userId = "555666777888999000".snowflake

// Lire un membre
val member = kda.getGuildMember(guildId, userId).getOrNull()

// Renommer + assigner des rôles
kda.modifyGuildMember(guildId, userId, ModifyMemberPayload(
    nick = "Nouveau pseudo",
    roles = listOf("111111111111111111".snowflake),
))

// Expulser (kick)
kda.removeGuildMember(guildId, userId)
