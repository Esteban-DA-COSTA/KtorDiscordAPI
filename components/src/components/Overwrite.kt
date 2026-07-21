package ktordiscord.components

import kotlinx.serialization.Serializable

/**
 * A permission overwrite on a channel (see Discord "Overwrite Object").
 *
 * Used both as a **received** model (on [Channel]) and as an outgoing payload element in
 * [ModifyChannelPayload] / [CreateChannelPayload]. [type] is `0` for a role overwrite and `1`
 * for a member overwrite. [allow]/[deny] are permission bitfields serialized as strings.
 */
@Serializable
data class Overwrite(
    val id: Snowflake,
    val type: Int,
    val allow: String? = null,
    val deny: String? = null,
)
