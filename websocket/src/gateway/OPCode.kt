package ktordiscord.gateway

import ktordiscord.components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OPCodeSerializer::class)
enum class OPCode(val opCode: Int) {
    DISPATCH(0),
    HEARTBEAT(1),
    IDENTIFY(2),
    PRESENCE_UPDATE(3),
    RESUME(6),
    REQUEST_GUILD_MEMBERS(8),
    RECONNECT(7),
    INVALID_SESSION(9),
    HELLO(10),
    HBACK(11),
    UNKNOWN(-1),
}

object OPCodeSerializer : IntEnumSerializer<OPCode>(
    "op", OPCode.entries.toTypedArray(), { it.opCode }, OPCode.UNKNOWN
)
