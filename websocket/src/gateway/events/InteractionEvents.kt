package ktordiscord.gateway.events

import ktordiscord.components.interactions.Interaction
import kotlinx.serialization.Serializable

@Serializable
data class InteractionCreateEvent(
    override var sequenceId: Int,
    val interaction: Interaction,
) : DispatchEvent()