package ktordiscord.components

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Verifies the [DiscordError] model decodes from realistic Discord 4xx error bodies, the way the
 * REST client does (`ignoreUnknownKeys = true`).
 */
class DiscordErrorDecodingTest {
    // Mirrors the REST Json config used to decode responses.
    private val restJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Test
    fun decodesErrorWithValidationDetails() {
        val json = """
            {
              "code": 50035,
              "message": "Invalid Form Body",
              "errors": {
                "content": { "_errors": [ { "code": "BASE_TYPE_REQUIRED", "message": "This field is required" } ] }
              }
            }
        """.trimIndent()

        val error = restJson.decodeFromString(DiscordError.serializer(), json)
        assertEquals(50035, error.code)
        assertEquals("Invalid Form Body", error.message)
        assertNotNull(error.errors)
    }

    @Test
    fun decodesErrorWithoutErrorsField() {
        val json = """{ "code": 10003, "message": "Unknown Channel" }"""

        val error = restJson.decodeFromString(DiscordError.serializer(), json)
        assertEquals(10003, error.code)
        assertEquals("Unknown Channel", error.message)
        assertNull(error.errors)
    }
}
