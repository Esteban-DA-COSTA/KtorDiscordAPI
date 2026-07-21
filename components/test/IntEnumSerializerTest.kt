package ktordiscord.components.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Contract tests for [IntEnumSerializer], the shared base for integer-encoded Discord enums.
 *
 * Locks down the two guarantees that motivated it (see Priority 1 / IMPROVEMENTS.md):
 * lookup **by id** (never by ordinal/index, so non-contiguous ids stay correct) and a
 * graceful [Fixture.UNKNOWN] fallback instead of throwing on values Discord adds later.
 */
class IntEnumSerializerTest {
    // Deliberately non-contiguous ids, not starting at 0/1, so an ordinal-based lookup would break.
    @Serializable(Fixture.Serializer::class)
    private enum class Fixture(val id: Int) {
        A(10),
        B(20),
        C(30),
        UNKNOWN(-1);

        companion object Serializer :
            IntEnumSerializer<Fixture>("Fixture", entries.toTypedArray(), { it.id }, UNKNOWN)
    }

    private val json = Json

    @Test
    fun serializesToId() {
        assertEquals("20", json.encodeToString(Fixture.serializer(), Fixture.B))
    }

    @Test
    fun deserializesByIdNotByIndex() {
        // "20" must resolve to B (id 20), not to entries[20] (out of bounds) nor an ordinal.
        assertEquals(Fixture.B, json.decodeFromString(Fixture.serializer(), "20"))
        assertEquals(Fixture.A, json.decodeFromString(Fixture.serializer(), "10"))
        assertEquals(Fixture.C, json.decodeFromString(Fixture.serializer(), "30"))
    }

    @Test
    fun unknownIdFallsBackToUnknown() {
        assertEquals(Fixture.UNKNOWN, json.decodeFromString(Fixture.serializer(), "999"))
        assertEquals(Fixture.UNKNOWN, json.decodeFromString(Fixture.serializer(), "0"))
    }

    @Test
    fun roundTripsKnownValues() {
        for (value in listOf(Fixture.A, Fixture.B, Fixture.C)) {
            val encoded = json.encodeToString(Fixture.serializer(), value)
            assertEquals(value, json.decodeFromString(Fixture.serializer(), encoded))
        }
    }
}
