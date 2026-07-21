package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

/**
 * Type of an outgoing bot [activity][ktordiscord.components.BotActivity].
 *
 * @see [Activity Types](https://discord.com/developers/docs/events/gateway-events#activity-object-activity-types)
 */
@Serializable(ActivityType.Serializer::class)
enum class ActivityType(val id: Int) {
    PLAYING(0),
    STREAMING(1),
    LISTENING(2),
    WATCHING(3),
    CUSTOM(4),
    COMPETING(5),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ActivityType>(
        "ActivityType", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
