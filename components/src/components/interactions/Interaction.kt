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
    @SerialName("application_id") val applicationId: Snowflake,
    val type: components.enums.InteractionTypes?,
    @Transient var data: InteractionData? = null,
    @SerialName("guild_id") val guildId: Snowflake?,
    val channel: Channel?,
    @SerialName("channel_id") val channelId: Snowflake?,
    val member: Member?,
    val user: User?,
    val token: String,
    val version: Int = 1,
    val message: Message?,
    @SerialName("app_permissions") val appPermissions: String?,
    val locale: String?,
    @SerialName("guild_locale") val guildLocale: String?,
    val entitlements: List<Entitlement>?,
    @SerialName("authorizing_integration_owners") val authorizingIntegrationOwners: Map<String, String>?,
    val context: components.enums.InteractionContextTypes?,
)

@Serializable(InteractionData.Serializer::class)
sealed class InteractionData {

    @OptIn(ExperimentalSerializationApi::class)
    @kotlinx.serialization.Serializer(forClass = InteractionData::class)
    companion object Serializer : KSerializer<InteractionData> {
        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("interactionData") {
                element<components.enums.InteractionTypes>("type")
                element<JsonElement>("data")
            }

        override fun deserialize(decoder: Decoder): InteractionData = decoder.decodeStructure(
            descriptor
        ) {
            var type: components.enums.InteractionTypes? = null
            var data: JsonElement? = null
            do {
                var index = decodeElementIndex(descriptor)
                when (index) {
                    0 -> type = decodeSerializableElement(
                        descriptor,
                        index,
                        components.enums.InteractionTypes.serializer()
                    )

                    1 -> data = decodeSerializableElement(
                        descriptor, index, JsonElement.serializer()
                    )
                }
            } while (index != CompositeDecoder.DECODE_DONE)
            val jsonDecoder = decoder as JsonDecoder
            when (type) {
                components.enums.InteractionTypes.APPLICATION_COMMAND -> {
                    jsonDecoder.json.decodeFromJsonElement<ApplicationCommand>(data!!)
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
    val resolved: ResolvedData? = null,
    val options: List<ApplicationCommandOption>? = null,
    @SerialName("guild_id") val guildId: Snowflake? = null,
    @SerialName("target_id") val targetId: Snowflake? = null,
    var description: String?
) : InteractionData()

@Serializable
data class MessageComponentData(
    @SerialName("custom_id") val customId: String,
    @SerialName("component_type") val componentType: Int = 0, // Enum componentType
)

@Serializable
data class ResolvedData(
    val users: Map<Snowflake, User>?,
    val members: Map<Snowflake, Member>?,
    val roles: Map<Snowflake, Role>?,
    val channels: Map<Snowflake, Channel>?,
    val messages: Map<Snowflake, Message>?,
    val attachments: Map<Snowflake, Attachment>?,
)

@Serializable
data class ApplicationCommandOption(
    val name: String,
    val type: ApplicationCommandOptionTypes,
    val value: String?,
    val options: List<ApplicationCommandOption>?,
    val focused: Boolean?,
)
