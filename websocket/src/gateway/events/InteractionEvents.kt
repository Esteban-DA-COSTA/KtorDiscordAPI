package gateway.events

import components.interactions.Interaction
import kotlinx.serialization.Serializable

@Serializable
data class InteractionCreateEvent(
    override var sequenceId: Int,
    val interaction: Interaction,
) : DispatchEvent()