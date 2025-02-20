package components

import components.enums.EmbedTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    var content: String? = null,
    var tts: Boolean = false,
    var embeds: MutableList<components.Embed>? = null,
    @SerialName("channel_id")
    val channelId: String? = null,
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