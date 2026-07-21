package ktordiscord.components

import ktordiscord.builders.DiscordDsl
import ktordiscord.components.enums.EmbedTypes
import ktordiscord.components.enums.MessageType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message **received** from Discord (e.g. via `MESSAGE_CREATE`). Immutable.
 *
 * To *send* a message, use [MessagePayload] (the target of the `sendMessage { }` DSL).
 */
@Serializable
data class Message(
    val id: Snowflake? = null,
    @SerialName("channel_id")
    val channelId: Snowflake? = null,
    val author: User? = null,
    val content: String? = null,
    val timestamp: String? = null,
    @SerialName("edited_timestamp")
    val editedTimestamp: String? = null,
    val tts: Boolean = false,
    @SerialName("mention_everyone")
    val mentionEveryone: Boolean = false,
    val mentions: List<User>? = null,
    @SerialName("mention_roles")
    val mentionRoles: List<Snowflake>? = null,
    val embeds: List<Embed>? = null,
    val reactions: List<Reaction>? = null,
    val pinned: Boolean = false,
    @SerialName("webhook_id")
    val webhookId: Snowflake? = null,
    val type: MessageType? = null,
    val attachments: List<Attachment>? = null,
    @SerialName("message_reference")
    val messageReference: MessageReference? = null,
    @SerialName("referenced_message")
    val referencedMessage: Message? = null,
    @SerialName("sticker_items")
    val stickerItems: List<StickerItem>? = null,
    val flags: Int? = null,
)

/**
 * Payload used to **send** a message to Discord. Mutable so the `sendMessage { }` DSL can fill it in.
 */
@Serializable
@DiscordDsl
data class MessagePayload(
    var content: String? = null,
    var tts: Boolean = false,
    var embeds: MutableList<ktordiscord.components.Embed>? = null,
    var components: MutableList<ktordiscord.components.MessageComponent>? = null,
    /**
     * Message flags bitfield. In an interaction response, `1 shl 6` marks the message *ephemeral*
     * (only the invoking user sees it). Left `null` (omitted) for a normal message.
     */
    var flags: Int? = null,
)

@Serializable
@DiscordDsl
data class Embed(
    var title: String? = null,
    var type: ktordiscord.components.enums.EmbedTypes? = null,
    var description: String? = null,
    var url: String? = null,
    var timestamps: String? = null,
    var color: Int? = null,
    var footer: ktordiscord.components.EmbedFooter? = null,
    var image: ktordiscord.components.EmbedImage? = null,
    var thumbnail: ktordiscord.components.EmbedImage? = null,
    var video: ktordiscord.components.EmbedImage? = null,
    var provider: ktordiscord.components.EmbedProvider? = null,
    var author: ktordiscord.components.EmbedAuthor? = null,
    var fields: MutableList<ktordiscord.components.EmbedField>? = null


)

@Serializable
@DiscordDsl
data class EmbedFooter(
    var text: String = "",
    @SerialName("icon_url")
    var iconUrl: String? = null,
    @SerialName("proxy_icon_url")
    var proxyIconUrl: String? = null
)

@Serializable
@DiscordDsl
data class EmbedImage(
    var url: String = "",
    @SerialName("proxy_url")
    var proxyUrl: String? = null,
    var height: Int? = null,
    var width: Int? = null
)

@Serializable
@DiscordDsl
data class EmbedProvider(
    var name: String? = null,
    var url: String? = null
)

@Serializable
@DiscordDsl
data class EmbedAuthor(
    var name: String = "",
    var url: String? = null,
    @SerialName("icon_url")
    var iconUrl: String? = null,
    @SerialName("proxy_icon_url")
    var proxyIconUrl: String? = null
)

@Serializable
@DiscordDsl
data class EmbedField(
    var name: String = "",
    var value: String = "",
    var inline: Boolean = false
)