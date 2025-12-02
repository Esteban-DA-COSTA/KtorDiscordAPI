package components.interactions

import components.*
import components.enums.ApplicationCommandTypes
import components.enums.InteractionTypes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable(InteractionData.Serializer::class)
sealed class InteractionData {
    @OptIn(ExperimentalSerializationApi::class)
    @kotlinx.serialization.Serializer(forClass = InteractionData::class)
    companion object Serializer : KSerializer<InteractionData> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("interactionData") {
                element<InteractionTypes>("type")
                element<JsonElement>("data")
            }

        override fun deserialize(decoder: Decoder): InteractionData = decoder.decodeStructure(
            descriptor
        ) {
            var type: InteractionTypes? = null
            var data: JsonElement? = null
            do {
                val index = decodeElementIndex(descriptor)
                when (index) {
                    0 -> type = decodeSerializableElement(descriptor, index, InteractionTypes.serializer())
                    1 -> data = decodeSerializableElement(descriptor, index, JsonElement.serializer())
                }
            } while (index != CompositeDecoder.DECODE_DONE)
            val jsonDecoder = decoder as JsonDecoder
            when (type) {
                InteractionTypes.APPLICATION_COMMAND -> {
                    jsonDecoder.json.decodeFromJsonElement<ApplicationCommandData>(data!!)
                }

                else -> TODO(type!!.name)
            }
        }
    }
}

@Serializable
data class ApplicationCommandData(
    val id: Snowflake,
    val name: String,
    var type: ApplicationCommandTypes = ApplicationCommandTypes.CHAT_INPUT,
    var resolved: ResolvedData? = null,
    var options: List<ApplicationCommandDataOption>? = null,
    @SerialName("guild_id")
    var guildId: Snowflake? = null,
    @SerialName("target_id")
    var targetId: Snowflake? = null,
    var description: String? = null
) : InteractionData()

@Serializable
data class MessageComponentData(
    @SerialName("custom_id")
    val customId: String,
    @SerialName("component_type")
    val componentType: Int = 0, // TODO Enum componentType
)

@Serializable
data class ResolvedData(
    val users: Map<Snowflake, User>?,
    val members: Map<Snowflake, Member>?,
    val roles: Map<Snowflake, Role>?,
    val channels: Map<Snowflake, Channel>?,
    val messages: Map<Snowflake, Message>?,
    val attachments: Map<Snowflake, Attachment>?
)

@Serializable
data class ApplicationCommandDataOption(
    val name: String,
//    val type: ApplicationCommandOptionTypes,
    val value: String?,
    val options: List<ApplicationCommandDataOption>?,
    val focused: Boolean?
)
