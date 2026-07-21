package ktordiscord.components.interactions

import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ApplicationCommandTypes
import ktordiscord.components.enums.InteractionContextTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationCommand(
    val id: Snowflake,
    val type: Int? = null, // Optional as per documentation
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake? = null, // Nullable for global application commands
    val name: String,
    var description: String? = null, // Nullable for command types without description
    var options: List<ApplicationCommandDataOption>? = null, // List representing possible options
    @SerialName("default_permission")
    var defaultMemberPermissions: String? = null, // Nullable permissions bitfield
    var context: List<InteractionContextTypes>? = null,
    var nsfw: Boolean? = null, // Nullable flag for 'not safe for work'
): DiscordCommand

/**
 * Payload used to **create/update** an application command. Unlike [ApplicationCommand] (the model
 * returned by Discord), it carries no `id` — Discord assigns it — and no mandatory `application_id`
 * (that goes in the URL).
 */
@Serializable
data class ApplicationCommandPayload(
    // Nullable so PATCH (edit) can omit it — the REST Json config has `encodeDefaults = false`, so a
    // null name is left out of the body entirely. Create always sets it (function parameter).
    var name: String? = null,
    var description: String? = null,
    val type: Int? = null,
    var options: List<ApplicationCommandDataOption>? = null,
    @SerialName("default_permission")
    var defaultMemberPermissions: String? = null,
    var context: List<InteractionContextTypes>? = null,
    var nsfw: Boolean? = null,
) : DiscordCommand

@Serializable
data class ApplicationCommandOption(
    val name: String,
    val type: ApplicationCommandTypes,
    var description: String,
    var required: Boolean? = null,
    val choices: List<String>? = null,
    val options: List<ApplicationCommandDataOption>? = null,
)