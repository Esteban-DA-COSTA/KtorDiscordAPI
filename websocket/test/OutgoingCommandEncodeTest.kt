package ktordiscord.gateway.events

import ktordiscord.components.BotActivity
import ktordiscord.components.Presence
import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ActivityType
import ktordiscord.components.enums.StatusTypeEnum
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pure-encoding tests for the outgoing Gateway commands, locking down the `{op, d}` envelope
 * produced by [OutgoingEventSerializer] and its subclasses (Identify/Resume retrofit + the new
 * Presence Update / Request Guild Members commands).
 */
class OutgoingCommandEncodeTest {
    // Mirrors the WebSocket Json config in DiscordClient.
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun presenceUpdateEncodesOp3Envelope() {
        val event = UpdatePresenceEvent(
            Presence(
                status = StatusTypeEnum.DO_NOT_DISTURB,
                activities = listOf(BotActivity("Ktor Discord API", ActivityType.PLAYING))
            )
        )

        val root = json.encodeToString(UpdatePresenceEvent.serializer(), event).let(json::parseToJsonElement).jsonObject

        assertEquals(3, root["op"]!!.jsonPrimitive.int)
        val d = root["d"]!!.jsonObject
        assertEquals("dnd", d["status"]!!.jsonPrimitive.content)
        val activity = d["activities"]!!.jsonArray.single().jsonObject
        assertEquals("Ktor Discord API", activity["name"]!!.jsonPrimitive.content)
        assertEquals(0, activity["type"]!!.jsonPrimitive.int)
    }

    @Test
    fun requestGuildMembersEncodesOp8Envelope() {
        val event = RequestGuildMembersEvent(RequestGuildMembersData(guildId = Snowflake("123456"), limit = 0))

        val root = json.encodeToString(RequestGuildMembersEvent.serializer(), event).let(json::parseToJsonElement).jsonObject

        assertEquals(8, root["op"]!!.jsonPrimitive.int)
        val d = root["d"]!!.jsonObject
        assertEquals("123456", d["guild_id"]!!.jsonPrimitive.content)
        assertEquals(0, d["limit"]!!.jsonPrimitive.int)
    }

    @Test
    fun identifyRetrofitStillEncodesOp2() {
        // Regression guard: Identify now shares OutgoingEventSerializer; opcode must stay 2.
        val root = json.encodeToString(IdentifyEvent.serializer(), IdentifyEvent("token", 513))
            .let(json::parseToJsonElement).jsonObject

        assertEquals(2, root["op"]!!.jsonPrimitive.int)
        assertEquals("token", root["d"]!!.jsonObject["token"]!!.jsonPrimitive.content)
    }
}
