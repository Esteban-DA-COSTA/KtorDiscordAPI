import ktordiscord.components.enums.StatusTypeEnum

kda.updatePresence {
    status = StatusTypeEnum.ONLINE
    watching("le serveur")          // "Regarde le serveur"
    // streaming("un live", "https://twitch.tv/…")
    // listening("vos commandes")
    // custom("🚀 en maintenance")
}
