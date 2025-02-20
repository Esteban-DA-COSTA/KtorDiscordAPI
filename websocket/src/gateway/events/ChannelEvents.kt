package gateway.events

import components.Channel
import kotlinx.serialization.Serializable

@Serializable
data class ChannelCreateEvent(
    override var sequenceId: Int = 0,
    val channel: Channel
) : DispatchEvent()

@Serializable
data class ChannelUpdateEvent(
    override var sequenceId: Int = 0,
    val channel: Channel
) : DispatchEvent()

@Serializable
data class ChannelDeleteEvent(
    override var sequenceId: Int = 0,
    val channel: Channel
) : DispatchEvent()