package ktordiscord.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for the component-routing logic that fixes the historical `componentHandlers` leak:
 * the bounded [BoundedHandlerCache] backing ephemeral `click { }` handlers, and the prefix/arg
 * split used by declared handlers. Pure logic, no HTTP (like `DiscordResponseTest`).
 */
class ComponentRoutingTest {

    // region BoundedHandlerCache — LRU
    @Test
    fun evictsLeastRecentlyUsedBeyondMaxSize() {
        val cache = BoundedHandlerCache<String>(maxSize = 2)
        cache.put("a", "va")
        cache.put("b", "vb")
        cache.put("c", "vc") // pushes past the cap → "a" (eldest) is evicted

        assertEquals(2, cache.size)
        assertNull(cache.get("a"))
        assertEquals("vb", cache.get("b"))
        assertEquals("vc", cache.get("c"))
    }

    @Test
    fun accessRefreshesLruOrder() {
        val cache = BoundedHandlerCache<String>(maxSize = 2)
        cache.put("a", "va")
        cache.put("b", "vb")
        cache.get("a")       // "a" becomes most-recently-used, so "b" is now the eldest
        cache.put("c", "vc") // evicts "b", not "a"

        assertEquals("va", cache.get("a"))
        assertNull(cache.get("b"))
        assertEquals("vc", cache.get("c"))
    }

    @Test
    fun staysBoundedUnderFlood() {
        val maxSize = 100
        val cache = BoundedHandlerCache<String>(maxSize = maxSize)
        repeat(10_000) { cache.put("id-$it", "v-$it") } // the old registry would have grown to 10_000

        assertEquals(maxSize, cache.size)
    }
    // endregion

    // region BoundedHandlerCache — TTL
    @Test
    fun expiresEntriesPastTtl() {
        var now = 0L
        val cache = BoundedHandlerCache<String>(maxSize = 8, ttlMs = 1_000, nowMs = { now })
        cache.put("a", "va")

        now = 1_000 // exactly at the TTL bound → still live (strictly-greater comparison)
        assertNotNull(cache.get("a"))

        now = 1_001 // past the TTL → treated as absent and dropped
        assertNull(cache.get("a"))
        assertEquals(0, cache.size)
    }
    // endregion

    // region Prefix / arg routing semantics
    @Test
    fun prefixRoutingParsesKeyAndArg() {
        // "approve:42" routes to the handler registered under "approve", state read from arg.
        assertEquals("approve", "approve:42".substringBefore(COMPONENT_ID_SEPARATOR))
        assertEquals("42", "approve:42".substringAfter(COMPONENT_ID_SEPARATOR, ""))

        // An id without a separator is its own prefix (exact match) and carries an empty arg.
        assertEquals("menu-refresh", "menu-refresh".substringBefore(COMPONENT_ID_SEPARATOR))
        assertEquals("", "menu-refresh".substringAfter(COMPONENT_ID_SEPARATOR, ""))

        // The payload may itself contain the separator; only the first one splits key from arg.
        assertEquals("route", "route:a:b:c".substringBefore(COMPONENT_ID_SEPARATOR))
        assertEquals("a:b:c", "route:a:b:c".substringAfter(COMPONENT_ID_SEPARATOR, ""))
    }
    // endregion
}
