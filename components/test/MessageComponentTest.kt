package ktordiscord.components

import ktordiscord.components.enums.ButtonStyle
import ktordiscord.components.enums.ComponentType
import ktordiscord.components.interactions.InteractionData
import ktordiscord.components.interactions.MessageComponentData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Locks down the message-component layer: buttons serialize to Discord's `{type, style, custom_id}`
 * shape inside an action row, and an incoming MESSAGE_COMPONENT interaction decodes to
 * [MessageComponentData].
 */
class MessageComponentTest {
    // Mirrors the REST Json config: default fields (e.g. tts=false) are omitted.
    private val restJson = Json {
        encodeDefaults = false
        explicitNulls = false
    }

    // Mirrors the WebSocket Json config used to decode incoming payloads.
    private val wsJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun buttonEncodesToDiscordShapeInsideActionRow() {
        val payload = MessagePayload().apply {
            content = "hi"
            components = mutableListOf(
                ActionRow(listOf(Button(style = ButtonStyle.PRIMARY, label = "Click", customId = "abc")))
            )
        }

        val root = restJson.encodeToJsonElement(MessagePayload.serializer(), payload).jsonObject
        val row = root["components"]!!.jsonArray[0].jsonObject
        assertEquals(1, row["type"]!!.jsonPrimitive.int)

        val button = row["components"]!!.jsonArray[0].jsonObject
        assertEquals(2, button["type"]!!.jsonPrimitive.int)
        assertEquals(1, button["style"]!!.jsonPrimitive.int) // PRIMARY
        assertEquals("Click", button["label"]!!.jsonPrimitive.content)
        assertEquals("abc", button["custom_id"]!!.jsonPrimitive.content)
        // Unset optional fields must be omitted, not sent as null.
        assertNull(button["url"])
        assertNull(button["disabled"])
    }

    @Test
    fun messageComponentInteractionDataDecodes() {
        val data = wsJson.decodeFromString(
            InteractionData.serializer(),
            """{"type":3,"data":{"custom_id":"my-button","component_type":2}}"""
        )
        assertIs<MessageComponentData>(data)
        assertEquals("my-button", data.customId)
        assertEquals(ComponentType.BUTTON, data.componentType)
    }

    @Test
    fun messageComponentInteractionDataFallsBackOnUnknownType() {
        val data = wsJson.decodeFromString(
            InteractionData.serializer(),
            """{"type":3,"data":{"custom_id":"future","component_type":99}}"""
        )
        assertIs<MessageComponentData>(data)
        assertEquals(ComponentType.UNKNOWN, data.componentType)
    }
}
