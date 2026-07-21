package ktordiscord.components

import ktordiscord.components.enums.ApplicationCommandPermissionType
import ktordiscord.components.interactions.ApplicationCommandPermission
import ktordiscord.components.interactions.ApplicationCommandPermissionsPayload
import ktordiscord.components.interactions.GuildApplicationCommandPermissions
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks down the application-command permission models: a real Discord payload decodes to
 * [GuildApplicationCommandPermissions] (permission type decoded by integer), and the edit body
 * serializes to Discord's `{ "permissions": [...] }` shape with integer types.
 */
class ApplicationCommandPermissionsTest {
    private val restJson = Json {
        encodeDefaults = false
        explicitNulls = false
    }

    private val wsJson = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun guildPermissionsDecodeWithIntegerType() {
        val perms = wsJson.decodeFromString(
            GuildApplicationCommandPermissions.serializer(),
            """
            {
              "id": "123",
              "application_id": "456",
              "guild_id": "789",
              "permissions": [
                { "id": "111", "type": 1, "permission": true },
                { "id": "222", "type": 2, "permission": false }
              ]
            }
            """.trimIndent()
        )

        assertEquals("123", perms.id.value)
        assertEquals("456", perms.applicationId.value)
        assertEquals("789", perms.guildId.value)
        assertEquals(2, perms.permissions.size)
        assertEquals(ApplicationCommandPermissionType.ROLE, perms.permissions[0].type)
        assertEquals(true, perms.permissions[0].permission)
        assertEquals(ApplicationCommandPermissionType.USER, perms.permissions[1].type)
        assertEquals(false, perms.permissions[1].permission)
    }

    @Test
    fun unknownPermissionTypeFallsBackToUnknown() {
        val perm = wsJson.decodeFromString(
            ApplicationCommandPermission.serializer(),
            """{ "id": "1", "type": 99, "permission": true }"""
        )
        assertEquals(ApplicationCommandPermissionType.UNKNOWN, perm.type)
    }

    @Test
    fun editPayloadEncodesToDiscordShape() {
        val payload = ApplicationCommandPermissionsPayload(
            listOf(ApplicationCommandPermission("333".snowflake, ApplicationCommandPermissionType.CHANNEL, true))
        )

        val root = restJson.encodeToJsonElement(ApplicationCommandPermissionsPayload.serializer(), payload).jsonObject
        val entry = root["permissions"]!!.jsonArray[0].jsonObject
        assertEquals("333", entry["id"]!!.jsonPrimitive.content)
        assertEquals(3, entry["type"]!!.jsonPrimitive.int) // CHANNEL serialized as its integer id
        assertEquals(true, entry["permission"]!!.jsonPrimitive.content.toBoolean())
    }
}
