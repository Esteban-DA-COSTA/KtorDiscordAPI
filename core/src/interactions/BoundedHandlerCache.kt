package ktordiscord.core

import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Thread-safe, bounded cache used for **ephemeral** component handlers (the closures registered by
 * `button(...).click { }` under an auto-generated `custom_id`). Unlike the developer-declared
 * `componentHandlers` registry — finite and controlled — these entries are created one per rendered
 * button and would otherwise grow without bound, pinning every closure-captured object for the whole
 * process lifetime (the historical `componentHandlers` memory leak).
 *
 * Two independent eviction policies keep it bounded:
 * - **LRU**: at most [maxSize] entries; inserting past the cap drops the least-recently-accessed one
 *   (backing [LinkedHashMap] in access order).
 * - **TTL**: an entry older than [ttlMs] is treated as absent and dropped lazily on lookup (no
 *   background thread). The default aligns with Discord's ~15 min interaction token lifetime: past it,
 *   a click could not be answered through that token anyway.
 *
 * Every operation is guarded by a single monitor: dispatch runs concurrent coroutines and [put] may
 * race response building. [nowMs] is injectable so the TTL policy can be tested without a real clock.
 */
internal class BoundedHandlerCache<V>(
    private val maxSize: Int = DEFAULT_MAX_SIZE,
    private val ttlMs: Long = DEFAULT_TTL_MS,
    private val nowMs: () -> Long = System::currentTimeMillis,
) {
    private class Entry<V>(val value: V, val insertedAtMs: Long)

    private val logger = KotlinLogging.logger("INTERACTION_LOGGER")
    private val lock = Any()

    // accessOrder = true → get() moves the entry to the most-recently-used end, so removeEldestEntry
    // evicts the genuinely least-recently-used key.
    private val entries = object : LinkedHashMap<String, Entry<V>>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry<V>>): Boolean {
            val evict = size > maxSize
            if (evict) logger.trace { "Evicting ephemeral component handler '${eldest.key}' (LRU, size=$size > maxSize=$maxSize)" }
            return evict
        }
    }

    fun put(key: String, value: V) = synchronized(lock) {
        entries[key] = Entry(value, nowMs())
        Unit
    }

    /** Returns the handler for [key], or `null` if absent or expired (an expired entry is dropped). */
    fun get(key: String): V? = synchronized(lock) {
        val entry = entries[key] ?: return null
        if (nowMs() - entry.insertedAtMs > ttlMs) {
            entries.remove(key)
            logger.trace { "Ephemeral component handler '$key' expired (TTL=${ttlMs}ms)" }
            return null
        }
        entry.value
    }

    /** Current number of live entries. Exposed for tests asserting the cache stays bounded. */
    val size: Int get() = synchronized(lock) { entries.size }

    companion object {
        /** Default LRU cap on ephemeral component handlers. */
        const val DEFAULT_MAX_SIZE = 1024

        /** Default TTL, aligned with Discord's ~15 min interaction token lifetime. */
        const val DEFAULT_TTL_MS = 15 * 60 * 1000L
    }
}
