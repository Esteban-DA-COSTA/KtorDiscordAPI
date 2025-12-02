package components

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@JvmInline
@Serializable(Snowflake.Serializer::class)
value class Snowflake(val value: String) {
    companion object Serializer : KSerializer<Snowflake> {
        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor("snowflake", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = Snowflake(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: Snowflake) = encoder.encodeString(value.value)
    }

    override fun toString(): String {
        return value
    }
}

/**
 * Extension property for the String class that converts a string value into a [Snowflake] instance.
 *
 * This property serves as a shorthand for constructing a [Snowflake] object,
 * which is used as a lightweight and type-safe representation of unique identifiers.
 *
 * The resulting [Snowflake] instance encapsulates the string value and provides
 * additional functionality, such as serialization. It is particularly useful in contexts
 * where unique identifiers (e.g., IDs for roles, users, or teams) are frequently used.
 */
val String.snowflake
    get() = Snowflake(this)