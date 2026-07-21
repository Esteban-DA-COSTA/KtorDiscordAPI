import ktordiscord.components.RolePayload
import ktordiscord.components.snowflake

val guildId = "987654321098765432".snowflake

// Créer un rôle
val role = kda.createNewRole(guildId, RolePayload(
    name = "Modérateur",
    hoist = true,        // affiché séparément dans la liste des membres
    mentionable = true,
)).getOrNull()

// Modifier / supprimer
role?.let {
    kda.editRole(guildId, it.id, RolePayload(name = "Modérateur+"))
    kda.deleteRole(guildId, it.id)
}
