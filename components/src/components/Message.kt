package components

import components.enums.EmbedTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message **received** from Discord (e.g. via `MESSAGE_CREATE`). Immutable.
 *
 * To *send* a message, use [MessagePayload] (the target of the `sendMessage { }` DSL).
 */
@Serializable
data class Message(
    val id: components.Snowflake? = null,
    @SerialName("channel_id")
    val channelId: String? = null,
    val author: components.User? = null,
    val content: String? = null,
    val timestamp: String? = null,
    @SerialName("edited_timestamp")
    val editedTimestamp: String? = null,
    val tts: Boolean = false,
    val embeds: List<components.Embed>? = null,
)

/**
 * Payload used to **send** a message to Discord. Mutable so the `sendMessage { }` DSL can fill it in.
 */
@Serializable
data class MessagePayload(
    var content: String? = null,
    var tts: Boolean = false,
    var embeds: MutableList<components.Embed>? = null,
)

@Serializable
data class Embed(
    var title: String? = null,
    var type: components.enums.EmbedTypes? = null,
    var description: String? = null,
    var url: String? = null,
    var timestamps: String? = null,
    var color: Int? = null,
    var footer: components.EmbedFooter? = null,
    var image: components.EmbedImage? = null,
    var thumbnail: components.EmbedImage? = null,
    var video: components.EmbedImage? = null,
    var provider: components.EmbedProvider? = null,
    var author: components.EmbedAuthor? = null,
    var fields: MutableList<components.EmbedField>? = null


)

@Serializable
data class EmbedFooter(
    var text: String = "",
    @SerialName("icon_url")
    var iconUrl: String? = null,
    @SerialName("proxy_icon_url")
    var proxyIconUrl: String? = null
)

@Serializable
data class EmbedImage(
    var url: String = "",
    @SerialName("proxy_url")
    var proxyUrl: String? = null,
    var height: Int? = null,
    var width: Int? = null
)

@Serializable
data class EmbedProvider(
    var name: String? = null,
    var url: String? = null
)

@Serializable
data class EmbedAuthor(
    var name: String = "",
    var url: String? = null,
    @SerialName("icon_url")
    var iconUrl: String? = null,
    @SerialName("proxy_icon_url")
    var proxyIconUrl: String? = null
)

@Serializable
data class EmbedField(
    var name: String = "",
    var value: String = "",
    var inline: Boolean = false
)