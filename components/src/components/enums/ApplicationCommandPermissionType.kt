package ktordiscord.components.enums

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(ApplicationCommandPermissionType.Serializer::class)
enum class ApplicationCommandPermissionType(val id: Int) {
    ROLE(1),
    USER(2),
    CHANNEL(3),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<ApplicationCommandPermissionType>(
        "ApplicationCommandPermissionType", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
