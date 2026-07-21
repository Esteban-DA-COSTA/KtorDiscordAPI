package ktordiscord.core

import io.ktor.http.HttpStatusCode
import ktordiscord.components.DiscordError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for the pure logic of [DiscordResponse] and its helpers (no HTTP involved).
 */
class DiscordResponseTest {

    private fun success(value: String) = DiscordResponse.Success(value, HttpStatusCode.OK)
    private fun failure(error: DiscordError? = null) = DiscordResponse.Failure(HttpStatusCode.NotFound, error)

    @Test
    fun getOrNullReturnsValueOnSuccess() {
        assertEquals("x", success("x").getOrNull())
    }

    @Test
    fun getOrNullReturnsNullOnFailure() {
        assertNull(failure().getOrNull())
    }

    @Test
    fun getOrThrowReturnsValueOnSuccess() {
        assertEquals("x", success("x").getOrThrow())
    }

    @Test
    fun getOrThrowThrowsOnFailure() {
        val error = DiscordError(code = 10003, message = "Unknown Channel")
        val thrown = assertFailsWith<DiscordApiException> { failure(error).getOrThrow() }
        assertEquals(HttpStatusCode.NotFound, thrown.status)
        assertEquals(10003, thrown.error?.code)
    }

    @Test
    fun isSuccessAndIsFailureFlags() {
        assertTrue(success("x").isSuccess)
        assertFalse(success("x").isFailure)
        assertTrue(failure().isFailure)
        assertFalse(failure().isSuccess)
    }

    @Test
    fun onSuccessRunsOnlyOnSuccess() {
        var seen: String? = null
        success("x").onSuccess { seen = it }
        assertEquals("x", seen)

        seen = null
        failure().onSuccess { seen = "should-not-run" }
        assertNull(seen)
    }

    @Test
    fun onFailureRunsOnlyOnFailure() {
        var seenStatus: HttpStatusCode? = null
        failure().onFailure { seenStatus = it.status }
        assertEquals(HttpStatusCode.NotFound, seenStatus)

        seenStatus = null
        success("x").onFailure { seenStatus = it.status }
        assertNull(seenStatus)
    }

    @Test
    fun onSuccessReturnsSelfForChaining() {
        val res = success("x")
        assertEquals(res, res.onSuccess { }.onFailure { })
    }
}
