package gateway

import components.serialization.IntEnumSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OPCodeSerializer::class)
enum class OPCode(val opCode: Int) {
    DISPATCH(0),
    HEARTBEAT(1),
    IDENTIFY(2),
    RESUME(6),
    RECONNECT(7),
    INVALID_SESSION(9),
    HELLO(10),
    HBACK(11),
    UNKNOWN(-1),
}

object OPCodeSerializer : IntEnumSerializer<OPCode>(
    "op", OPCode.entries.toTypedArray(), { it.opCode }, OPCode.UNKNOWN
)
