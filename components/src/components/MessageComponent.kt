package ktordiscord.components

import ktordiscord.components.enums.ButtonStyle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * A Discord message component (action row, button, …).
 *
 * The model is **write-oriented**: the library only *sends* components. Incoming component
 * interactions carry just a `custom_id`/`component_type`, decoded as
 * [ktordiscord.components.interactions.MessageComponentData] — never as a full [MessageComponent].
 * Deserializing a [MessageComponent] therefore throws.
 */
@Serializable(MessageComponent.Serializer::class)
sealed class MessageComponent {
    abstract val type: Int

    companion object Serializer : KSerializer<MessageComponent> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MessageComponent")

        override fun serialize(encoder: Encoder, value: MessageComponent) {
            val jsonEncoder = encoder as? JsonEncoder
                ?: error("MessageComponent can only be serialized to JSON")
            val element = buildJsonObject {
                put("type", value.type)
                when (value) {
                    is ActionRow -> put(
                        "components",
                        jsonEncoder.json.encodeToJsonElement(
                            ListSerializer(MessageComponent.Serializer),
                            value.components
                        )
                    )

                    is Button -> {
                        put("style", value.style.id)
                        value.label?.let { put("label", it) }
                        value.customId?.let { put("custom_id", it) }
                        value.url?.let { put("url", it) }
                        value.disabled?.let { put("disabled", it) }
                        value.emoji?.let { emoji ->
                            put("emoji", buildJsonObject {
                                emoji.name?.let { put("name", it) }
                                emoji.id?.let { put("id", it.toString()) }
                                emoji.animated?.let { put("animated", it) }
                            })
                        }
                    }
                }
            }
            jsonEncoder.encodeJsonElement(element)
        }

        override fun deserialize(decoder: Decoder): MessageComponent =
            error("Deserializing MessageComponent is not supported")
    }
}

/**
 * A container that holds up to 5 child components on a single row (`type = 1`).
 * Discord requires interactive components (buttons) to be wrapped in an action row.
 */
data class ActionRow(
    val components: List<MessageComponent> = emptyList()
) : MessageComponent() {
    override val type: Int = 1
}

/**
 * A clickable button (`type = 2`).
 *
 * Non-[ButtonStyle.LINK] buttons need a [customId]; [ButtonStyle.LINK] buttons need a [url] instead.
 */
data class Button(
    var style: ButtonStyle = ButtonStyle.PRIMARY,
    var label: String? = null,
    var customId: String? = null,
    var url: String? = null,
    var emoji: Emoji? = null,
    var disabled: Boolean? = null,
) : MessageComponent() {
    override val type: Int = 2
}
