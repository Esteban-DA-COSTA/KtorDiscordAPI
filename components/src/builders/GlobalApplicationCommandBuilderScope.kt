package builders

import components.Snowflake
import components.enums.ApplicationCommandTypes
import components.enums.InteractionContextTypes
import components.interactions.ApplicationCommand
import components.interactions.ApplicationCommandDataOption

class GlobalApplicationCommandBuilderScope(val name: String, val types: ApplicationCommandTypes) {
    var id: Snowflake = Snowflake("-1")
    var applicationId: Snowflake = Snowflake("-1")
    private val guildId: Snowflake? = null
    var description: String? = null
    var options: List<ApplicationCommandDataOption>? = null
    var defaultMemberPermissions: String? = null
    var context: List<InteractionContextTypes>? = null
    var nsfw: Boolean? = null

    fun build() = ApplicationCommand(id, types, applicationId, guildId, name, description, options, defaultMemberPermissions, context, nsfw)

}