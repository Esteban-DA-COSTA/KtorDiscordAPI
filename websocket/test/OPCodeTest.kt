package ktordiscord.gateway

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * (De)serialization tests for [OPCode] via [OPCodeSerializer]. Gateway opcodes are non-contiguous
 * (there is no opcode 4/5, RECONNECT is 7 but declared after REQUEST_GUILD_MEMBERS), so decoding
 * must go by the `opCode` value — never by ordinal — and unknown opcodes must fall back.
 */
class OPCodeTest {
    private val json = Json

    @Test
    fun serializesToOpCodeValue() {
        assertEquals("0", json.encodeToString(OPCode.serializer(), OPCode.DISPATCH))
        assertEquals("10", json.encodeToString(OPCode.serializer(), OPCode.HELLO))
        assertEquals("8", json.encodeToString(OPCode.serializer(), OPCode.REQUEST_GUILD_MEMBERS))
    }

    @Test
    fun deserializesByOpCodeNotByIndex() {
        assertEquals(OPCode.HELLO, json.decodeFromString(OPCode.serializer(), "10"))
        // RECONNECT is declared after REQUEST_GUILD_MEMBERS but has opcode 7: proves value-based lookup.
        assertEquals(OPCode.RECONNECT, json.decodeFromString(OPCode.serializer(), "7"))
        assertEquals(OPCode.REQUEST_GUILD_MEMBERS, json.decodeFromString(OPCode.serializer(), "8"))
    }

    @Test
    fun unknownOpCodeFallsBackToUnknown() {
        assertEquals(OPCode.UNKNOWN, json.decodeFromString(OPCode.serializer(), "42"))
        // Gaps in the opcode range (4, 5) are unknown too.
        assertEquals(OPCode.UNKNOWN, json.decodeFromString(OPCode.serializer(), "4"))
    }

    @Test
    fun roundTripsEveryKnownOpCode() {
        for (op in OPCode.entries.filter { it != OPCode.UNKNOWN }) {
            val encoded = json.encodeToString(OPCode.serializer(), op)
            assertEquals(op, json.decodeFromString(OPCode.serializer(), encoded))
        }
    }
}
