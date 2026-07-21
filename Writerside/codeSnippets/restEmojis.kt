import ktordiscord.components.CreateEmojiPayload
import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake

// Lister
val emojis = kda.listGuildEmojis(guildId).getOrNull()

// Créer (image en data URI base64)
kda.createGuildEmoji(guildId, CreateEmojiPayload(
    name = "party",
    image = "data:image/png;base64,iVBORw0KGgo…",
))
