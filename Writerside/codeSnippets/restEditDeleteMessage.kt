import ktordiscord.components.MessagePayload
import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake
val messageId = "111222333444555666".snowflake

// Modifier
kda.editChannelMessage(channelId, messageId, MessagePayload(content = "Édité !"))

// Supprimer
kda.deleteChannelMessage(channelId, messageId)

// Supprimer en masse (jusqu'à 100, < 2 semaines)
kda.bulkDeleteMessages(channelId, listOf(messageId, "111222333444555777".snowflake))
