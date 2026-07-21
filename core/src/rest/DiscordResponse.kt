package ktordiscord.core

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import ktordiscord.components.DiscordError

/**
 * Typed result of a Discord REST call.
 *
 * Every REST function returns a [DiscordResponse] carrying both the HTTP [status] and, on success,
 * the deserialized body. Consumers can branch exhaustively with `when`, or use the convenience
 * helpers [getOrNull], [getOrThrow], [onSuccess] and [onFailure].
 *
 * ```
 * when (val res = client.getChannel(id)) {
 *     is DiscordResponse.Success -> println(res.value.name)
 *     is DiscordResponse.Failure -> log.warn { "${res.status}: ${res.error?.message}" }
 * }
 *
 * val channel = client.getChannel(id).getOrNull()
 * client.getChannel(id).onSuccess { println(it.name) }.onFailure { println(it.status) }
 * ```
 */
sealed interface DiscordResponse<out T> {
    /** The HTTP status of the response. */
    val status: HttpStatusCode

    /** A successful (2xx) response carrying the deserialized [value]. */
    data class Success<out T>(val value: T, override val status: HttpStatusCode) : DiscordResponse<T>

    /** A failed (non-2xx) response carrying the Discord [error] body when one was returned. */
    data class Failure(override val status: HttpStatusCode, val error: DiscordError?) : DiscordResponse<Nothing>

    /** `true` if this is a [Success]. */
    val isSuccess: Boolean get() = this is Success

    /** `true` if this is a [Failure]. */
    val isFailure: Boolean get() = this is Failure
}

/**
 * Thrown by [getOrThrow] when the response is a [DiscordResponse.Failure].
 *
 * @property status the HTTP status of the failed response.
 * @property error the Discord error body, when one was returned.
 */
class DiscordApiException(
    val status: HttpStatusCode,
    val error: DiscordError?,
) : RuntimeException("Discord API error $status: ${error?.message ?: "no error body"}")

/** The value on [DiscordResponse.Success], or `null` on [DiscordResponse.Failure]. */
fun <T> DiscordResponse<T>.getOrNull(): T? = (this as? DiscordResponse.Success)?.value

/** The value on [DiscordResponse.Success], or throw [DiscordApiException] on [DiscordResponse.Failure]. */
fun <T> DiscordResponse<T>.getOrThrow(): T = when (this) {
    is DiscordResponse.Success -> value
    is DiscordResponse.Failure -> throw DiscordApiException(status, error)
}

/** Run [action] with the value if this is a [DiscordResponse.Success]; returns `this` for chaining. */
inline fun <T> DiscordResponse<T>.onSuccess(action: (T) -> Unit): DiscordResponse<T> {
    if (this is DiscordResponse.Success) action(value)
    return this
}

/** Run [action] with the failure if this is a [DiscordResponse.Failure]; returns `this` for chaining. */
inline fun <T> DiscordResponse<T>.onFailure(action: (DiscordResponse.Failure) -> Unit): DiscordResponse<T> {
    if (this is DiscordResponse.Failure) action(this)
    return this
}

/**
 * Decode a raw [HttpResponse] into a typed [DiscordResponse]: the body as [T] on 2xx, otherwise a
 * [DiscordResponse.Failure] with the Discord error body (best-effort — a missing/non-JSON error
 * body yields `error = null`).
 */
internal suspend inline fun <reified T> HttpResponse.decode(): DiscordResponse<T> =
    if (status.isSuccess()) DiscordResponse.Success(body<T>(), status)
    else DiscordResponse.Failure(status, runCatching { body<DiscordError>() }.getOrNull())

/**
 * Decode a raw [HttpResponse] with no useful body (204 No Content, DELETE/PUT side-effects) into a
 * [DiscordResponse]`<Unit>`.
 */
internal suspend fun HttpResponse.decodeEmpty(): DiscordResponse<Unit> =
    if (status.isSuccess()) DiscordResponse.Success(Unit, status)
    else DiscordResponse.Failure(status, runCatching { body<DiscordError>() }.getOrNull())
