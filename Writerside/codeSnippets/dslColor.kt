import java.awt.Color
import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    embed {
        title = "Coloré"
        color(Color(0x5865F2))   // Blurple Discord
        // ou une constante : color(Color.RED)
    }
}
