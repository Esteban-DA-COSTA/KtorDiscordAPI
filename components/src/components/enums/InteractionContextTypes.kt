package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(ktordiscord.components.enums.InteractionContextTypes.Serializer::class)
enum class InteractionContextTypes(val id: Int) {
    GUILD(0),
    BOT_DM(1),
    PRIVATE_CHANNEL(2),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<InteractionContextTypes>(
        "InteractionContextTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
