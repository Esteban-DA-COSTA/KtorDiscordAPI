package gateway.events

data class InvalidSessionEvent(val resumable: Boolean) : Event()
