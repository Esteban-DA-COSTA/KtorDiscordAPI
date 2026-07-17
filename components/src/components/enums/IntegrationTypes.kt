package components.enums

import components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(components.enums.IntegrationTypes.Serializer::class)
enum class IntegrationTypes(val id: Int) {
    GUILD_INSTALL(0),
    USER_INSTALL(1),
    UNKNOWN(-1);

    companion object Serializer : IntEnumSerializer<IntegrationTypes>(
        "IntegrationTypes", entries.toTypedArray(), { it.id }, UNKNOWN
    )
}
