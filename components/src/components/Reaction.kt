package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A reaction on a message (see Discord "Reaction Object"), as returned e.g. on a [Message]
 * or in a `MESSAGE_REACTION_ADD` event.
 *
 * @property count total number of times this emoji has been used to react.
 * @property me whether the current user reacted using this emoji.
 * @property emoji partial emoji information.
 */
@Serializable
data class Reaction(
    val count: Int,
    val me: Boolean,
    val emoji: Emoji,
    @SerialName("count_details") val countDetails: ReactionCountDetails? = null,
)

/**
 * Breakdown of a [Reaction] count between normal and super (burst) reactions.
 */
@Serializable
data class ReactionCountDetails(
    val burst: Int,
    val normal: Int,
)
