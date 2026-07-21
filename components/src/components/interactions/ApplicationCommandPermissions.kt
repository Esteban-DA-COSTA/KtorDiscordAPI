package ktordiscord.components.interactions

import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ApplicationCommandPermissionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Returned by Discord: the set of permissions applied to a command (or, when [id] equals the
 * application id, the application-wide default) in a given guild.
 */
@Serializable
data class GuildApplicationCommandPermissions(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val permissions: List<ApplicationCommandPermission>,
)

/**
 * A single permission entry: allow/deny a role, user or channel for a command.
 *
 * @property id id of the role, user or channel this entry targets (special sentinels exist for
 *   "@everyone" and "all channels" — see the Discord docs).
 * @property type what [id] refers to.
 * @property permission `true` to allow, `false` to deny.
 */
@Serializable
data class ApplicationCommandPermission(
    val id: Snowflake,
    val type: ApplicationCommandPermissionType,
    val permission: Boolean,
)

/** Body of the "edit application command permissions" endpoint (`{ "permissions": [...] }`). */
@Serializable
data class ApplicationCommandPermissionsPayload(
    val permissions: List<ApplicationCommandPermission>,
)
