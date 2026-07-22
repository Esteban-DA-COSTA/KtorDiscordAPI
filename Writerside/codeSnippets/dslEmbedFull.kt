import java.awt.Color
import ktordiscord.components.snowflake

val channelId = "123456789012345678".snowflake

kda.sendMessage(channelId) {
    content = "Rapport du jour"
    embed {
        title = "Statistiques du serveur"
        description = "Résumé de l'activité des dernières 24 h."
        url = "https://example.com/stats"
        timestamps = "2026-07-22T10:00:00.000Z"   // horodatage ISO 8601
        color(Color(0x5865F2))                     // Blurple Discord

        author {
            name = "KDA Bot"
            url = "https://example.com"
            iconUrl = "https://example.com/avatar.png"
        }

        thumbnail { url = "https://example.com/thumb.png" }
        image { url = "https://example.com/graph.png" }

        field {
            name = "Membres"
            value = "1 337"
            inline = true
        }
        field {
            name = "Messages"
            value = "42 000"
            inline = true
        }

        footer {
            text = "Généré automatiquement"
            iconUrl = "https://example.com/icon.png"
        }
    }
}
