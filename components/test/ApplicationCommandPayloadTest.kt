package ktordiscord.components

import ktordiscord.components.enums.ApplicationCommandOptionTypes
import ktordiscord.components.enums.ApplicationCommandTypes
import ktordiscord.components.enums.InteractionContextTypes
import ktordiscord.components.interactions.ApplicationCommandOption
import ktordiscord.components.interactions.ApplicationCommandOptionChoice
import ktordiscord.components.interactions.ApplicationCommandPayload
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Locks down the create/update payload shape after the model cleanup: command/option types encode as
 * integers, snake_case keys match Discord (`default_member_permissions`, `contexts`), choice values
 * keep their JSON type, and unset optional fields are omitted (not sent as null).
 */
class ApplicationCommandPayloadTest {
    // Mirrors the REST Json config used to send request bodies.
    private val restJson = Json {
        encodeDefaults = false
        explicitNulls = false
    }

    @Test
    fun payloadWithOptionsAndChoicesEncodesToDiscordShape() {
        val payload = ApplicationCommandPayload(name = "echo").apply {
            description = "Echo a value"
            type = ApplicationCommandTypes.CHAT_INPUT
            defaultMemberPermissions = "2048"
            contexts = listOf(InteractionContextTypes.GUILD, InteractionContextTypes.BOT_DM)
            options = listOf(
                ApplicationCommandOption(
                    type = ApplicationCommandOptionTypes.STRING,
                    name = "text",
                    description = "the text",
                    required = true,
                    choices = listOf(
                        ApplicationCommandOptionChoice.of("one", "1"),
                        ApplicationCommandOptionChoice.of("two", 2),
                    ),
                )
            )
        }

        val root = restJson.encodeToJsonElement(ApplicationCommandPayload.serializer(), payload).jsonObject

        assertEquals("echo", root["name"]!!.jsonPrimitive.content)
        assertEquals(1, root["type"]!!.jsonPrimitive.int) // CHAT_INPUT as integer id
        assertEquals("2048", root["default_member_permissions"]!!.jsonPrimitive.content)
        // `contexts` (plural), integer-encoded context types.
        assertEquals(listOf(0, 1), root["contexts"]!!.jsonArray.map { it.jsonPrimitive.int })

        val option = root["options"]!!.jsonArray[0].jsonObject
        assertEquals(3, option["type"]!!.jsonPrimitive.int) // STRING as integer id
        assertEquals("text", option["name"]!!.jsonPrimitive.content)
        assertEquals(true, option["required"]!!.jsonPrimitive.content.toBoolean())

        val choices = option["choices"]!!.jsonArray
        assertEquals("1", choices[0].jsonObject["value"]!!.jsonPrimitive.content) // string value stays a string
        assertEquals(2, choices[1].jsonObject["value"]!!.jsonPrimitive.int)       // int value stays an int

        // Unset optionals must be omitted, not null.
        assertNull(root["nsfw"])
        assertNull(root["dm_permission"])
        assertNull(option["autocomplete"])
    }

    @Test
    fun editPayloadOmitsNameWhenUnset() {
        // Edit (PATCH) style: only description set — name must not be sent as an empty string.
        val payload = ApplicationCommandPayload().apply { description = "new desc" }
        val root = restJson.encodeToJsonElement(ApplicationCommandPayload.serializer(), payload).jsonObject
        assertNull(root["name"])
        assertEquals("new desc", root["description"]!!.jsonPrimitive.content)
    }
}
