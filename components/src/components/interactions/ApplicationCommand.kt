package components.interactions

import components.Snowflake
import components.enums.ApplicationCommandTypes
import components.enums.InteractionContextTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApplicationCommand(
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
): DiscordCommand {
    override operator fun equals(other: Any?): Boolean {
        return when (other) {
            is ApplicationCommand -> id == other.id || name == other.name
            is ApplicationCommandData -> id == other.id || name == other.name
            is Snowflake -> id == other
            is String -> name == other
            else -> super.equals(other)
        }
    }
}

@Serializable
data class ApplicationCommandOption(
    val name: String,
    val type: ApplicationCommandTypes,
    var description: String,
    var required: Boolean? = null,
    val choices: List<String>? = null,
    val options: List<ApplicationCommandDataOption>? = null,
)