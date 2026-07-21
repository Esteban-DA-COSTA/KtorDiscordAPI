import ktordiscord.components.CreateChannelPayload
import ktordiscord.components.enums.ChannelTypes
import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake

val channels = kda.getGuildChannels(guildId).getOrNull()

kda.createGuildChannel(guildId, CreateChannelPayload(
    name = "annonces",
    type = ChannelTypes.GUILD_TEXT,
))
