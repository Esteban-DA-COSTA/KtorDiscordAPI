package gateway.events

import kotlinx.serialization.Transient

sealed class DispatchEvent : Event() {
    @Transient
    abstract var sequenceId: Int

}