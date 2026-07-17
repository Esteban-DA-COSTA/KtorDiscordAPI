package components.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Base serializer for Discord enums that are encoded as integers in JSON.
 *
 * Looks up entries by their [idOf] value — never by ordinal/index, which breaks
 * as soon as the ids are non-contiguous — and falls back to [unknown] instead of
 * throwing when Discord sends a value the enum does not yet know about.
 *
 * Usage: give the enum a `val id: Int` and an `UNKNOWN(-1)` entry, then let its
 * companion object extend this class:
 * ```
 * companion object Serializer : IntEnumSerializer<MyEnum>(
 *     "MyEnum", entries.toTypedArray(), { it.id }, UNKNOWN
 * )
 * ```
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
abstract class IntEnumSerializer<T : Enum<T>>(
    serialName: String,
    private val entries: Array<T>,
    private val idOf: (T) -> Int,
    private val unknown: T
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor(serialName, PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): T {
        val id = decoder.decodeInt()
        return entries.firstOrNull { idOf(it) == id } ?: unknown
    }

    override fun serialize(encoder: Encoder, value: T) = encoder.encodeInt(idOf(value))
}
