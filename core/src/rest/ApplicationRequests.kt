package ktordiscord.core

import ktordiscord.components.Application
import ktordiscord.components.Snowflake
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

suspend fun DiscordClient.getApplication(applicationId: Snowflake): DiscordResponse<Application> {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId") {
        buildDiscordHeader(token)
    }.decode()
}

suspend fun DiscordClient.getMeApplicationId(): Snowflake {
    val response = httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/@me") {
        buildDiscordHeader(token)
    }
    val jsonBody = response.body<JsonElement>()
    val primitive = jsonBody.jsonObject["id"] as JsonPrimitive
    return Snowflake(primitive.content)
}
