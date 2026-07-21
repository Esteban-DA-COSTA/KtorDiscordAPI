package ktordiscord.gateway.events

import ktordiscord.gateway.OPCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

/**
 * Base serializer for outgoing Gateway commands, which all share the same `{ "op": <int>, "d": … }`
 * envelope. Factors out the boilerplate each command otherwise duplicates (see [IdentifySerializer],
 * [ResumeSerializer]), the same way [ktordiscord.components.serialization.IntEnumSerializer] factors
 * integer-coded enums.
 *
 * These events are send-only: [deserialize] always throws.
 *
 * Usage — let the command's serializer object extend this class:
 * ```
 * object IdentifySerializer : OutgoingEventSerializer<IdentifyEvent, IdentifyData>(
 *     "identify", OPCode.IDENTIFY, IdentifyData.serializer(), { it.data }
 * )
 * ```
 *
 * @param E the outgoing [Event] type.
 * @param D the type of its `d` payload.
 * @param opCode the Gateway opcode written under `op`.
 * @param dataSerializer the serializer for the `d` payload.
 * @param dataOf extracts the `d` payload from the event instance.
 */
abstract class OutgoingEventSerializer<E : Event, D>(
    private val serialName: String,
    private val opCode: OPCode,
    private val dataSerializer: KSerializer<D>,
    private val dataOf: (E) -> D
) : KSerializer<E> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serialName) {
        element("op", OPCode.serializer().descriptor)
        element("d", dataSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: E) = encoder.encodeStructure(descriptor) {
        encodeSerializableElement(descriptor, 0, OPCode.serializer(), opCode)
        encodeSerializableElement(descriptor, 1, dataSerializer, dataOf(value))
    }

    override fun deserialize(decoder: Decoder): E =
        throw UnsupportedOperationException("$serialName is an outgoing-only event")
}
