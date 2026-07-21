package ktordiscord.gateway.events

import ktordiscord.components.VoiceState
import kotlinx.serialization.Serializable

/**
 * `VOICE_STATE_UPDATE`: a user's voice state changed (joined, moved, left, muted…). The payload is
 * a voice state object; a `null` [VoiceState.channelId] means the user left the voice channel.
 */
@Serializable
data class VoiceStateUpdateEvent(
    override var sequenceId: Int,
    val voiceState: VoiceState,
) : DispatchEvent()
