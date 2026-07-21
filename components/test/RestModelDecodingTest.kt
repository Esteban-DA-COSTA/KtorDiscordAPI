package ktordiscord.components

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Verifies the new received models decode from realistic Discord REST responses, tolerating unknown
 * keys the way the REST client does (`ignoreUnknownKeys = true`).
 */
class RestModelDecodingTest {
    // Mirrors the REST Json config used to decode responses.
    private val restJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun reactionDecodesWithEmojiAndCountDetails() {
        val json = """
            {
              "count": 3,
              "me": true,
              "count_details": { "burst": 1, "normal": 2 },
              "emoji": { "id": null, "name": "🔥" }
            }
        """.trimIndent()

        val reaction = restJson.decodeFromString(Reaction.serializer(), json)
        assertEquals(3, reaction.count)
        assertTrue(reaction.me)
        assertEquals("🔥", reaction.emoji.name)
        assertEquals(2, reaction.countDetails?.normal)
    }

    @Test
    fun banDecodesWithNullReason() {
        val json = """
            {
              "reason": null,
              "user": { "id": "1234567890", "username": "banned_user" }
            }
        """.trimIndent()

        val ban = restJson.decodeFromString(Ban.serializer(), json)
        assertNull(ban.reason)
        assertEquals(Snowflake("1234567890"), ban.user.id)
        assertEquals("banned_user", ban.user.username)
    }

    @Test
    fun messageDecodesEnrichedFields() {
        val json = """
            {
              "id": "999",
              "channel_id": "55",
              "content": "hey",
              "type": 0,
              "pinned": true,
              "mention_everyone": false,
              "mention_roles": ["1", "2"],
              "attachments": [
                { "id": "7", "filename": "a.png", "size": 123, "url": "http://x/a.png", "proxy_url": "http://y/a.png" }
              ],
              "reactions": [
                { "count": 2, "me": false, "emoji": { "id": null, "name": "👍" } }
              ]
            }
        """.trimIndent()

        val message = restJson.decodeFromString(Message.serializer(), json)
        assertEquals(Snowflake("999"), message.id)
        assertEquals(Snowflake("55"), message.channelId)
        assertEquals(ktordiscord.components.enums.MessageType.DEFAULT, message.type)
        assertTrue(message.pinned)
        assertEquals(listOf(Snowflake("1"), Snowflake("2")), message.mentionRoles)
        assertEquals("a.png", message.attachments?.single()?.filename)
        assertEquals(2, message.reactions?.single()?.count)
    }

    @Test
    fun memberDecodesEnrichedFields() {
        val json = """
            {
              "nick": "Bob",
              "roles": ["1", "2"],
              "joined_at": "2020-01-01T00:00:00Z",
              "premium_since": "2021-06-01T00:00:00Z",
              "pending": true,
              "flags": 0,
              "communication_disabled_until": "2030-01-01T00:00:00Z",
              "deaf": false,
              "mute": false
            }
        """.trimIndent()

        val member = restJson.decodeFromString(Member.serializer(), json)
        assertEquals("Bob", member.nick)
        assertEquals(listOf(Snowflake("1"), Snowflake("2")), member.roles)
        assertEquals("2021-06-01T00:00:00Z", member.premiumSince)
        assertTrue(member.pending)
        assertEquals("2030-01-01T00:00:00Z", member.communicationDisabledUntil)
    }

    @Test
    fun overwriteDecodesPermissionBitfields() {
        val json = """{ "id": "555", "type": 0, "allow": "1024", "deny": "0" }"""
        val overwrite = restJson.decodeFromString(Overwrite.serializer(), json)
        assertEquals(Snowflake("555"), overwrite.id)
        assertEquals(0, overwrite.type)
        assertEquals("1024", overwrite.allow)
    }
}
