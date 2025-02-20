package gateway.events

import components.*
import components.interactions.Interaction
import components.interactions.InteractionData
import gateway.DispatchEvents
import gateway.DispatchEvents.*
import gateway.OPCode
import gateway.OPCode.DISPATCH
import gateway.OPCode.HBACK
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = EventSerializer::class)
sealed class Event

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Event::class)
private object EventSerializer : KSerializer<Event> {
    private val logger = KotlinLogging.logger("WSS_LOGGER")
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
        element("op", OPCode.serializer().descriptor)
        element("t", String.serializer().descriptor.nullable, isOptional = true)
        element("s", Int.serializer().descriptor.nullable, isOptional = true)
        element("d", JsonElement.serializer().descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): Event = decoder.decodeStructure(descriptor) {
        var op: OPCode? = null
        var t: String? = null
        var s: Int? = null
        var d: JsonElement? = null
        do {
            val index = decodeElementIndex(descriptor)
            when (index) {
                0 -> op = decodeSerializableElement(descriptor, index, OPCode.serializer(), op)
                1 -> t = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable, t)
                2 -> s = decodeNullableSerializableElement(descriptor, index, Int.serializer().nullable, s)
                3 -> d = decodeSerializableElement(descriptor, index, JsonElement.serializer(), d)
            }

        } while (index != CompositeDecoder.DECODE_DONE)

        if (op == DISPATCH)
            decodeDispatchEvent(decoder, t!!, s!!, d!!)
        else
            decodeClassicEvent(op!!, decoder, d)
    }

    private fun decodeDispatchEvent(
        decoder: Decoder,
        eventName: String,
        sequenceId: Int,
        data: JsonElement
    ): DispatchEvent {
        val dispatchEvent = DispatchEvents.valueOf(eventName)
        decoder as JsonDecoder
        logger.info { "DISPATCH: $dispatchEvent" }
        return when (dispatchEvent) {
            READY -> decoder.json.decodeFromJsonElement(ReadyEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_CREATE -> {
                val message = decoder.json.decodeFromJsonElement(Message.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.toString()
                val member = decoder.json.decodeFromJsonElement(Member.serializer(), data.jsonObject["member"]!!)
                val mentions =
                    decoder.json.decodeFromJsonElement(ListSerializer(User.serializer()), data.jsonObject["mentions"]!!)
                MessageCreateEvent(sequenceId, message, guildId, member, mentions)
            }

            MESSAGE_UPDATE -> {
                val message = decoder.json.decodeFromJsonElement(Message.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.toString()
                val member = decoder.json.decodeFromJsonElement(Member.serializer(), data.jsonObject["member"]!!)
                val mentions =
                    decoder.json.decodeFromJsonElement(ListSerializer(User.serializer()), data.jsonObject["mentions"]!!)
                MessageUpdateEvent(sequenceId, message, guildId, member, mentions)
            }

            MESSAGE_DELETE -> decoder.json.decodeFromJsonElement(MessageDeleteEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_CREATE -> {
                val guild = decoder.json.decodeFromJsonElement(Guild.serializer(), data)
                GuildCreateEvent(sequenceId, guild)
            }

            PRESENCE_UPDATE -> decoder.json.decodeFromJsonElement(PresenceUpdateEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            CHANNEL_CREATE -> {
                val channel = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ChannelCreateEvent(sequenceId, channel)
            }

            CHANNEL_UPDATE -> {
                val channel = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ChannelUpdateEvent(sequenceId, channel)
            }

            CHANNEL_DELETE -> {
                val channel = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ChannelDeleteEvent(sequenceId, channel)
            }

            INTERACTION_CREATE -> {
                val interaction = decoder.json.decodeFromJsonElement(Interaction.serializer(), data)
                val interactionData = decoder.json.decodeFromJsonElement(InteractionData.serializer(), data)
                interaction.data = interactionData
                InteractionCreateEvent(sequenceId, interaction)
            }
        }
    }

    private fun decodeClassicEvent(opCode: OPCode, decoder: Decoder, data: JsonElement?): Event {
        val jsonDecoder = decoder as JsonDecoder
        return when (opCode) {
            OPCode.HELLO ->
                jsonDecoder.json.decodeFromJsonElement(HelloEvent.serializer(), data!!)

            HBACK -> {
                HBackEvent()
            }

            else -> TODO()
        }
    }
}