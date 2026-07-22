import ktordiscord.components.Emoji
import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    content = "Réagis :"

    // Emoji unicode : un simple name suffit.
    button("J'aime") {
        style = ButtonStyle.PRIMARY
        emoji = Emoji(name = "👍")
    }

    // Emoji custom de serveur : id + name (+ animated pour un emoji animé).
    button("Star") {
        style = ButtonStyle.SECONDARY
        emoji = Emoji(id = "123456789012345678".snowflake, name = "star")
    }

    // Bouton grisé, non cliquable.
    button("Indisponible") {
        style = ButtonStyle.SECONDARY
        disabled = true
    }
}
