package ktordiscord.components.interactions

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ApplicationCommandOptionTypes
import ktordiscord.components.enums.ApplicationCommandTypes
import ktordiscord.components.enums.ChannelTypes
import ktordiscord.components.enums.IntegrationTypes
import ktordiscord.components.enums.InteractionContextTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * An application command as **returned** by Discord. Immutable identity fields ([id],
 * [applicationId], [version]) are always present; the rest mirror what was defined.
 */
@Serializable
data class ApplicationCommand(
    val id: Snowflake,
    val type: ApplicationCommandTypes? = null, // Optional as per documentation; defaults to CHAT_INPUT
    @SerialName("application_id")
    val applicationId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake? = null, // Nullable for global application commands
    val name: String,
    @SerialName("name_localizations")
    var nameLocalizations: Map<String, String>? = null,
    var description: String? = null, // Nullable for command types without description
    @SerialName("description_localizations")
    var descriptionLocalizations: Map<String, String>? = null,
    var options: List<ApplicationCommandOption>? = null,
    @SerialName("default_member_permissions")
    var defaultMemberPermissions: String? = null, // Nullable permissions bitfield (string)
    @SerialName("dm_permission")
    var dmPermission: Boolean? = null, // Deprecated by `contexts`, still returned for old commands
    @SerialName("integration_types")
    var integrationTypes: List<IntegrationTypes>? = null,
    @SerialName("contexts")
    var contexts: List<InteractionContextTypes>? = null,
    var nsfw: Boolean? = null, // Nullable flag for 'not safe for work'
    val version: Snowflake? = null, // Autoincrementing version id, bumped on every update
) : DiscordCommand

/**
 * Payload used to **create/update** an application command. Unlike [ApplicationCommand] (the model
 * returned by Discord), it carries no `id` — Discord assigns it — and no mandatory `application_id`
 * (that goes in the URL).
 */
@Serializable
@DiscordDsl
data class ApplicationCommandPayload(
    // Nullable so PATCH (edit) can omit it — the REST Json config has `encodeDefaults = false`, so a
    // null name is left out of the body entirely. Create always sets it (function parameter).
    var name: String? = null,
    @SerialName("name_localizations")
    var nameLocalizations: Map<String, String>? = null,
    var description: String? = null,
    @SerialName("description_localizations")
    var descriptionLocalizations: Map<String, String>? = null,
    var type: ApplicationCommandTypes? = null,
    var options: List<ApplicationCommandOption>? = null,
    @SerialName("default_member_permissions")
    var defaultMemberPermissions: String? = null,
    @SerialName("dm_permission")
    var dmPermission: Boolean? = null,
    @SerialName("integration_types")
    var integrationTypes: List<IntegrationTypes>? = null,
    @SerialName("contexts")
    var contexts: List<InteractionContextTypes>? = null,
    var nsfw: Boolean? = null,
) : DiscordCommand

/**
 * A command option (an argument of a slash command, or a sub-command/-group). Used when **defining**
 * a command — distinct from [ApplicationCommandDataOption], which carries the *values* received when
 * a command is invoked.
 */
@Serializable
data class ApplicationCommandOption(
    val type: ApplicationCommandOptionTypes,
    val name: String,
    var description: String,
    @SerialName("name_localizations")
    var nameLocalizations: Map<String, String>? = null,
    @SerialName("description_localizations")
    var descriptionLocalizations: Map<String, String>? = null,
    var required: Boolean? = null,
    var choices: List<ApplicationCommandOptionChoice>? = null,
    var options: List<ApplicationCommandOption>? = null, // nested options for sub-commands/-groups
    @SerialName("channel_types")
    var channelTypes: List<ChannelTypes>? = null, // when type == CHANNEL
    @SerialName("min_value")
    var minValue: Double? = null, // for INTEGER / NUMBER options
    @SerialName("max_value")
    var maxValue: Double? = null,
    @SerialName("min_length")
    var minLength: Int? = null, // for STRING options
    @SerialName("max_length")
    var maxLength: Int? = null,
    var autocomplete: Boolean? = null,
)

/**
 * A predefined choice for a STRING/INTEGER/NUMBER option. Discord allows the [value] to be a string,
 * integer or double — hence [JsonPrimitive]; use the [of] helpers to build one from a Kotlin value.
 */
@Serializable
data class ApplicationCommandOptionChoice(
    val name: String,
    val value: JsonPrimitive,
    @SerialName("name_localizations")
    var nameLocalizations: Map<String, String>? = null,
) {
    companion object {
        fun of(name: String, value: String) = ApplicationCommandOptionChoice(name, JsonPrimitive(value))
        fun of(name: String, value: Int) = ApplicationCommandOptionChoice(name, JsonPrimitive(value))
        fun of(name: String, value: Double) = ApplicationCommandOptionChoice(name, JsonPrimitive(value))
    }
}
