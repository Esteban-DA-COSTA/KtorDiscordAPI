package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(ApplicationCommandTypes.ApplicationCommandTypesSerializer::class)
enum class ApplicationCommandTypes(val id: Int) {
    CHAT_INPUT(1),
    USER(2),
    MESSAGE(3),
    PRIMARY_ENTRY_POINT(4),
    UNKNOWN(-1);

    companion object ApplicationCommandTypesSerializer : IntEnumSerializer<ApplicationCommandTypes>(
        "ApplicationCommandTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
