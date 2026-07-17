package gateway.events

import components.enums.ChannelTypes
import components.enums.InteractionTypes
import gateway.OPCode
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Pure-decoding tests locking down Priority 1 robustness fixes:
 * unknown events/opcodes/enum values must fall back instead of throwing,
 * and MESSAGE_CREATE must decode correctly in DMs and read guild_id without quotes.
 */
class EventDecodeTest {
    // Mirrors the WebSocket Json config in DiscordClient.
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun unknownIntEnumFallsBackToUnknown() {
        // Out-of-range ids must not throw (exercises IntEnumSerializer + enum init order).
        assertEquals(ChannelTypes.UNKNOWN, json.decodeFromString(ChannelTypes.serializer(), "99"))
        assertEquals(InteractionTypes.UNKNOWN, json.decodeFromString(InteractionTypes.serializer(), "99"))
        // Known values still resolve by id, not by index.
        assertEquals(ChannelTypes.DM, json.decodeFromString(ChannelTypes.serializer(), "1"))
        assertEquals(InteractionTypes.APPLICATION_COMMAND, json.decodeFromString(InteractionTypes.serializer(), "2"))
    }

    @Test
    fun unknownDispatchEventDoesNotThrow() {
        val event = json.decodeFromString(
            Event.serializer(),
            """{"op":0,"t":"TYPING_START","s":5,"d":{"foo":"bar"}}"""
        )
        assertIs<UnknownDispatchEvent>(event)
        assertEquals("TYPING_START", event.name)
        assertEquals(5, event.sequenceId)
    }

    @Test
    fun unknownOpcodeDoesNotThrow() {
        val event = json.decodeFromString(Event.serializer(), """{"op":42,"d":null}""")
        assertIs<UnknownEvent>(event)
        assertEquals(OPCode.UNKNOWN, event.opCode)
    }

    @Test
    fun messageCreateInDmHasNoMemberAndNullGuild() {
        val event = json.decodeFromString(
            Event.serializer(),
            """{"op":0,"t":"MESSAGE_CREATE","s":1,"d":{"content":"hi"}}"""
        )
        assertIs<MessageCreateEvent>(event)
        assertNull(event.guildId)
        assertNull(event.member)
        assertNull(event.mentions)
        assertEquals("hi", event.message.content)
    }

    @Test
    fun messageCreateGuildIdHasNoQuotes() {
        val event = json.decodeFromString(
            Event.serializer(),
            """{"op":0,"t":"MESSAGE_CREATE","s":2,"d":{"content":"hi","guild_id":"123456"}}"""
        )
        assertIs<MessageCreateEvent>(event)
        assertEquals("123456", event.guildId)
    }
}
