import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake
val messageId = "111222333444555666".snowflake

// Émoji unicode
kda.createReaction(channelId, messageId, "👍")

// Émoji custom au format name:id
kda.createReaction(channelId, messageId, "partyblob:112233445566778899")

// Retirer la réaction du bot
kda.deleteOwnReaction(channelId, messageId, "👍")
