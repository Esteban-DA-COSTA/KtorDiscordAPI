package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(ktordiscord.components.enums.ChannelTypes.Serializer::class)
enum class ChannelTypes(val id: Int) {
    GUILD_TEXT(0),
    DM(1),
    GUILD_VOICE(2),
    GROUP_DM(3),
    GUILD_CATEGORY(4),
    GUILD_ANNOUNCEMENT(5),
    ANNOUNCEMENT_THREAD(10),
    PUBLIC_THREAD(11),
    PRIVATE_THREAD(12),
    GUILD_STAGE_VOICE(13),
    GUILD_DIRECTORY(14),
    GUILD_FORUM(15),
    GUILD_MEDIA(16),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ChannelTypes>(
        "ChannelTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
