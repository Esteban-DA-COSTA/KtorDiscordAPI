package gateway.events

import gateway.OPCode
import kotlinx.serialization.json.JsonElement

/**
 * Fallback for a dispatch event whose name is not (yet) listed in [gateway.DispatchEvents].
 *
 * Kept as a [DispatchEvent] so its sequence id is still tracked for resume and it is
 * forwarded to the events channel; consumers can inspect [name]/[data] or ignore it.
 */
class UnknownDispatchEvent(
    override var sequenceId: Int,
    val name: String,
    val data: JsonElement
) : DispatchEvent()

/**
 * Fallback for a Gateway payload whose opcode is not handled by the library.
 */
class UnknownEvent(
    val opCode: OPCode,
    val data: JsonElement?
) : Event()
