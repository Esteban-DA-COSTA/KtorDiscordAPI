import io.ktor.client.request.*
import io.ktor.http.*

fun HttpRequestBuilder.buildDiscordHeader(token: String) {
    headers {
        append(HttpHeaders.Authorization, "Bot $token")
    }
}