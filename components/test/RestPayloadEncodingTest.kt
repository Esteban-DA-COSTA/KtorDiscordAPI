package ktordiscord.components

import ktordiscord.components.enums.ChannelTypes
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Locks down the REST request-body shape for the new endpoints: snake_case keys, integer-encoded
 * enums (channel type), and — crucially — unset optional fields omitted rather than sent as null.
 */
class RestPayloadEncodingTest {
    // Mirrors the REST Json config used to send request bodies.
    private val restJson = Json {
        encodeDefaults = false
        explicitNulls = false
    }

    @Test
    fun modifyChannelPayloadOmitsUnsetFields() {
        val payload = ModifyChannelPayload().apply {
            name = "renamed"
            rateLimitPerUser = 5
        }
        val root = restJson.encodeToJsonElement(ModifyChannelPayload.serializer(), payload).jsonObject

        assertEquals("renamed", root["name"]!!.jsonPrimitive.content)
        assertEquals(5, root["rate_limit_per_user"]!!.jsonPrimitive.int)
        // Everything else is unset → omitted.
        assertNull(root["topic"])
        assertNull(root["nsfw"])
        assertNull(root["parent_id"])
        assertNull(root["permission_overwrites"])
    }

    @Test
    fun createChannelPayloadEncodesTypeAsInteger() {
        val payload = CreateChannelPayload(name = "voice-room").apply {
            type = ChannelTypes.GUILD_VOICE
            userLimit = 10
        }
        val root = restJson.encodeToJsonElement(CreateChannelPayload.serializer(), payload).jsonObject

        assertEquals("voice-room", root["name"]!!.jsonPrimitive.content)
        assertEquals(2, root["type"]!!.jsonPrimitive.int) // GUILD_VOICE id
        assertEquals(10, root["user_limit"]!!.jsonPrimitive.int)
        assertNull(root["topic"])
    }

    @Test
    fun bulkDeleteMessagesPayloadWrapsIdsUnderMessages() {
        val root = restJson.encodeToJsonElement(
            BulkDeleteMessagesPayload.serializer(),
            BulkDeleteMessagesPayload(listOf(Snowflake("111"), Snowflake("222"))),
        ).jsonObject
        assertEquals(listOf("111", "222"), root["messages"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    @Test
    fun modifyMemberPayloadOmitsUnsetFields() {
        val payload = ModifyMemberPayload().apply { nick = "The Bot" }
        val root = restJson.encodeToJsonElement(ModifyMemberPayload.serializer(), payload).jsonObject

        assertEquals("The Bot", root["nick"]!!.jsonPrimitive.content)
        assertNull(root["roles"])
        assertNull(root["channel_id"])
        assertNull(root["communication_disabled_until"])
    }

    @Test
    fun createBanPayloadEncodesDeleteMessageSeconds() {
        val root = restJson.encodeToJsonElement(
            CreateBanPayload.serializer(),
            CreateBanPayload(deleteMessageSeconds = 3600),
        ).jsonObject
        assertEquals(3600, root["delete_message_seconds"]!!.jsonPrimitive.int)
    }

    @Test
    fun createDMPayloadUsesRecipientIdKey() {
        val root = restJson.encodeToJsonElement(
            CreateDMPayload.serializer(),
            CreateDMPayload(recipientId = Snowflake("42")),
        ).jsonObject
        assertEquals("42", root["recipient_id"]!!.jsonPrimitive.content)
    }
}
