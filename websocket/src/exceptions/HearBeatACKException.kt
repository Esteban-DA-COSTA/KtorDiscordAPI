package exceptions

class HearBeatACKException(override val message: String? = "No heartbeat ACK received") : Exception(message) {
}
