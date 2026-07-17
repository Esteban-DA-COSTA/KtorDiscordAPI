package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(ktordiscord.components.enums.InteractionTypes.Serializer::class)
enum class InteractionTypes(val id: Int) {
    PING(1),
    APPLICATION_COMMAND(2),
    MESSAGE_COMPONENT(3),
    APPLICATION_COMMAND_AUTOCOMPLETE(4),
    MODAL_SUBMIT(5),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<InteractionTypes>(
        "InteractionTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
