package gateway

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
}

@OptIn(InternalSerializationApi::class)
object OPCodeSerializer : KSerializer<OPCode> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("op", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): OPCode = getByCode(decoder.decodeInt())
    override fun serialize(encoder: Encoder, value: OPCode) = encoder.encodeInt(value.opCode)

    private fun getByCode(op: Int): OPCode {
        return OPCode.entries.firstOrNull { op == it.opCode }
            ?: throw IllegalArgumentException("Unknown OPCode: $op")
    }
}