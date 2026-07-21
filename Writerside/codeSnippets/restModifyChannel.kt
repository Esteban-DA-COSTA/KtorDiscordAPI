import ktordiscord.components.ModifyChannelPayload
import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.modifyChannel(channelId, ModifyChannelPayload(
    name = "salon-renomme",
    topic = "Nouveau sujet du salon",
))

// Supprimer le salon (ou fermer un DM)
kda.deleteChannel(channelId)
