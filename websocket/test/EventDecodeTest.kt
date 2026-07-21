package ktordiscord.gateway.events

import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ChannelTypes
import ktordiscord.components.enums.InteractionTypes
import ktordiscord.components.enums.MessageType
import ktordiscord.gateway.OPCode
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
            """{"op":0,"t":"WEBHOOKS_UPDATE","s":5,"d":{"foo":"bar"}}"""
        )
        assertIs<UnknownDispatchEvent>(event)
        assertEquals("WEBHOOKS_UPDATE", event.name)
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
    fun messageCreateDecodesEnrichedFields() {
        // A realistic reply message carrying the newly-typed fields must decode fully (no `components`).
        val event = json.decodeFromString(
            Event.serializer(),
            """{"op":0,"t":"MESSAGE_CREATE","s":3,"d":{
                "id":"999","channel_id":"55","content":"hey","type":19,"pinned":true,
                "mention_everyone":false,"mention_roles":["1","2"],"flags":0,
                "attachments":[{"id":"7","filename":"a.png","size":123,"url":"http://x/a.png","proxy_url":"http://y/a.png"}],
                "message_reference":{"message_id":"888","channel_id":"55"},
                "referenced_message":{"id":"888","content":"original"}
            }}"""
        )
        assertIs<MessageCreateEvent>(event)
        val message = event.message
        assertEquals(MessageType.REPLY, message.type)
        assertTrue(message.pinned)
        assertEquals(listOf(Snowflake("1"), Snowflake("2")), message.mentionRoles)
        assertEquals("a.png", message.attachments?.single()?.filename)
        assertEquals(Snowflake("888"), message.messageReference?.messageId)
        assertEquals("original", message.referencedMessage?.content)
    }

    @Test
    fun messageCreateGuildIdHasNoQuotes() {
        val event = json.decodeFromString(
            Event.serializer(),
            """{"op":0,"t":"MESSAGE_CREATE","s":2,"d":{"content":"hi","guild_id":"123456"}}"""
        )
        assertIs<MessageCreateEvent>(event)
        assertEquals(Snowflake("123456"), event.guildId)
    }
}
