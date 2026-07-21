package ktordiscord.components

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pure (de)serialization tests for [Snowflake]: it must round-trip as a JSON *string*
 * (Discord ids overflow Int/Long precision, hence the String backing), and the
 * `String.snowflake` shorthand must build an equal instance.
 */
class SnowflakeTest {
    private val json = Json

    @Test
    fun serializesAsJsonString() {
        // Encoded with quotes: it is a JSON string, never a bare number.
        assertEquals("\"123456789\"", json.encodeToString(Snowflake.serializer(), Snowflake("123456789")))
    }

    @Test
    fun deserializesFromJsonString() {
        assertEquals(Snowflake("987654321"), json.decodeFromString(Snowflake.serializer(), "\"987654321\""))
    }

    @Test
    fun roundTripsLargeId() {
        // A real Discord snowflake, larger than Int.MAX_VALUE.
        val id = Snowflake("308994132968210433")
        val encoded = json.encodeToString(Snowflake.serializer(), id)
        assertEquals(id, json.decodeFromString(Snowflake.serializer(), encoded))
    }

    @Test
    fun snowflakeExtensionBuildsEqualInstance() {
        assertEquals(Snowflake("42"), "42".snowflake)
    }

    @Test
    fun toStringReturnsRawValue() {
        assertEquals("42", Snowflake("42").toString())
    }
}
