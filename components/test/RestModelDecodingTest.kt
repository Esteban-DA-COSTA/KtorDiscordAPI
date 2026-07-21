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
              "user": { "id": 1234567890, "username": "banned_user" }
            }
        """.trimIndent()

        val ban = restJson.decodeFromString(Ban.serializer(), json)
        assertNull(ban.reason)
        assertEquals(1234567890L, ban.user.id)
        assertEquals("banned_user", ban.user.username)
    }

    @Test
    fun overwriteDecodesPermissionBitfields() {
        val json = """{ "id": "555", "type": 0, "allow": "1024", "deny": "0" }"""
        val overwrite = restJson.decodeFromString(Overwrite.serializer(), json)
        assertEquals("555", overwrite.id)
        assertEquals(0, overwrite.type)
        assertEquals("1024", overwrite.allow)
    }
}
