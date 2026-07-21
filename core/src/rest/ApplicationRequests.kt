package ktordiscord.core

import ktordiscord.components.Snowflake
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

suspend fun DiscordClient.getApplication(applicationId: String): HttpResponse {
    return httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/$applicationId") {
        buildDiscordHeader(token)
    }
}

suspend fun DiscordClient.getMeApplicationId(): Snowflake {
    val response = httpClient.get("$discordURL/${DiscordEndpoints.APPLICATIONS.text}/@me") {
        buildDiscordHeader(token)
    }
    val jsonBody = response.body<JsonElement>()
    val primitive = jsonBody.jsonObject["id"] as JsonPrimitive
    return Snowflake(primitive.content)
}
