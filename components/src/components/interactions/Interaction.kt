package components.interactions

import components.*
import components.enums.ApplicationCommandOptionTypes
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class Interaction(
    val id: Snowflake,
    @SerialName("application_id")
    val applicationId: Snowflake,
    val type: components.enums.InteractionTypes?,
    @Transient
    var data: components.interactions.InteractionData? = null,
    @SerialName("guild_id")
    val guildId: Snowflake?,
    val channel: components.Channel?,
    @SerialName("channel_id")
    val channelId: Snowflake?,
    val member: components.Member?,
    val user: User?,
    val token: String,
    val version: Int = 1,
    val message: components.Message?,
    @SerialName("app_permissions")
    val appPermissions: String?,
    val locale: String?,
    @SerialName("guild_locale")
    val guildLocale: String?,
    val entitlements: List<components.Entitlement>?,
    @SerialName("authorizing_integration_owners")
    val authorizingIntegrationOwners: Map<String, String>?,
    val context: components.enums.InteractionContextTypes?
)

@Serializable(components.interactions.InteractionData.Serializer::class)
sealed class InteractionData {
    @OptIn(ExperimentalSerializationApi::class)
    @kotlinx.serialization.Serializer(forClass = components.interactions.InteractionData::class)
    companion object Serializer : KSerializer<components.interactions.InteractionData> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("interactionData") {
                element<components.enums.InteractionTypes>("type")
                element<JsonElement>("data")
            }

        override fun deserialize(decoder: Decoder): components.interactions.InteractionData = decoder.decodeStructure(
            components.interactions.InteractionData.Serializer.descriptor
        ) {
            var type: components.enums.InteractionTypes? = null
            var data: JsonElement? = null
            do {
                val index = decodeElementIndex(components.interactions.InteractionData.Serializer.descriptor)
                when (index) {
                    0 -> type = decodeSerializableElement(components.interactions.InteractionData.Serializer.descriptor, index, components.enums.InteractionTypes.serializer())
                    1 -> data = decodeSerializableElement(components.interactions.InteractionData.Serializer.descriptor, index, JsonElement.serializer())
                }
            } while (index != CompositeDecoder.DECODE_DONE)
            val jsonDecoder = decoder as JsonDecoder
            when (type) {
                components.enums.InteractionTypes.APPLICATION_COMMAND -> {
                    jsonDecoder.json.decodeFromJsonElement<components.interactions.ApplicationCommand>(data!!)
                }

                else -> TODO(type!!.name)
            }
        }
    }
}

@Serializable
data class ApplicationCommand(
    val id: Snowflake,
    val name: String,
    val type: Int,
    val resolved: components.interactions.ResolvedData?,
    val options: List<components.interactions.ApplicationCommandOption>?,
    @SerialName("guild_id")
    val guildId: Snowflake?,
    @SerialName("target_id")
    val targetId: Snowflake?,
) : components.interactions.InteractionData()

@Serializable
data class MessageComponentData(
    @SerialName("custom_id")
    val customId: String,
    @SerialName("component_type")
    val componentType: Int = 0, // Enum componentType
)

@Serializable
data class ResolvedData(
    val users: Map<Snowflake, User>?,
    val members: Map<Snowflake, components.Member>?,
    val roles: Map<Snowflake, components.Role>?,
    val channels: Map<Snowflake, components.Channel>?,
    val messages: Map<Snowflake, components.Message>?,
    val attachments: Map<Snowflake, Attachment>?
)

@Serializable
data class ApplicationCommandOption(
    val name: String,
    val type: ApplicationCommandOptionTypes,
    val value: String?,
    val options: List<components.interactions.ApplicationCommandOption>?,
    val focused: Boolean?
)
