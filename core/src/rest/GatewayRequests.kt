package ktordiscord.core

import io.ktor.client.request.*
import io.ktor.client.statement.*

private suspend fun DiscordClient.getGateway(): HttpResponse {
    return httpClient.get("$discordURL/gateway") {
        buildDiscordHeader(token)
    }
}
