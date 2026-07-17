package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(InteractionCallbackTypes.InteractionsCallbackTypesSerializer::class)
enum class InteractionCallbackTypes(val id: Int) {
    PONG(1),
    CHANNEL_MESSAGE_WITH_SOURCE(4),
    DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),
    DEFERRED_UPDATE_MESSAGE(6),
    UPDATE_MESSAGE(7),
    APPLICATION_COMMAND_AUTOCOMPLETE_RESULT(8),
    MODAL(9),
    UNKNOWN(-1);

    companion object InteractionsCallbackTypesSerializer : IntEnumSerializer<InteractionCallbackTypes>(
        "InteractionCallbackTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
