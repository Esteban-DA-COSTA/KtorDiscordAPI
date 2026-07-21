package ktordiscord.gateway.events

import ktordiscord.components.*
import ktordiscord.components.interactions.Interaction
import ktordiscord.components.interactions.InteractionData
import ktordiscord.gateway.DispatchEvents
import ktordiscord.gateway.DispatchEvents.*
import ktordiscord.gateway.OPCode
import ktordiscord.gateway.OPCode.DISPATCH
import ktordiscord.gateway.OPCode.HBACK
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
import kotlinx.serialization.json.boolean
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
        val dispatchEvent = DispatchEvents.entries.find { it.name == eventName }
            ?: run {
                logger.debug { "Unknown dispatch event: $eventName" }
                return UnknownDispatchEvent(sequenceId, eventName, data)
            }
        decoder as JsonDecoder
        logger.info { "DISPATCH: $dispatchEvent" }
        return when (dispatchEvent) {
            READY -> decoder.json.decodeFromJsonElement(ReadyEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_CREATE -> {
                val message = decoder.json.decodeFromJsonElement(Message.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.content?.let { Snowflake(it) }
                val member = data.jsonObject["member"]
                    ?.let { decoder.json.decodeFromJsonElement(Member.serializer(), it) }
                val mentions = data.jsonObject["mentions"]
                    ?.let { decoder.json.decodeFromJsonElement(ListSerializer(User.serializer()), it) }
                MessageCreateEvent(sequenceId, message, guildId, member, mentions)
            }

            MESSAGE_UPDATE -> {
                val message = decoder.json.decodeFromJsonElement(Message.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.content?.let { Snowflake(it) }
                val member = data.jsonObject["member"]
                    ?.let { decoder.json.decodeFromJsonElement(Member.serializer(), it) }
                val mentions = data.jsonObject["mentions"]
                    ?.let { decoder.json.decodeFromJsonElement(ListSerializer(User.serializer()), it) }
                MessageUpdateEvent(sequenceId, message, guildId, member, mentions)
            }

            MESSAGE_DELETE -> decoder.json.decodeFromJsonElement(MessageDeleteEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_DELETE_BULK -> decoder.json.decodeFromJsonElement(MessageDeleteBulkEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_CREATE -> {
                val guild = decoder.json.decodeFromJsonElement(Guild.serializer(), data)
                GuildCreateEvent(sequenceId, guild)
            }

            GUILD_UPDATE -> {
                val guild = decoder.json.decodeFromJsonElement(Guild.serializer(), data)
                GuildUpdateEvent(sequenceId, guild)
            }

            GUILD_DELETE -> {
                val guild = decoder.json.decodeFromJsonElement(UnavailableGuild.serializer(), data)
                GuildDeleteEvent(sequenceId, guild)
            }

            GUILD_MEMBER_ADD -> {
                val member = decoder.json.decodeFromJsonElement(Member.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.content?.let { Snowflake(it) }
                GuildMemberAddEvent(sequenceId, guildId, member)
            }

            GUILD_MEMBER_UPDATE -> decoder.json.decodeFromJsonElement(GuildMemberUpdateEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_MEMBER_REMOVE -> decoder.json.decodeFromJsonElement(GuildMemberRemoveEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_MEMBERS_CHUNK -> decoder.json.decodeFromJsonElement(GuildMembersChunkEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_ROLE_CREATE -> decoder.json.decodeFromJsonElement(GuildRoleCreateEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_ROLE_UPDATE -> decoder.json.decodeFromJsonElement(GuildRoleUpdateEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            GUILD_ROLE_DELETE -> decoder.json.decodeFromJsonElement(GuildRoleDeleteEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_REACTION_ADD -> decoder.json.decodeFromJsonElement(MessageReactionAddEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_REACTION_REMOVE -> decoder.json.decodeFromJsonElement(MessageReactionRemoveEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_REACTION_REMOVE_ALL -> decoder.json.decodeFromJsonElement(MessageReactionRemoveAllEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            MESSAGE_REACTION_REMOVE_EMOJI -> decoder.json.decodeFromJsonElement(MessageReactionRemoveEmojiEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            TYPING_START -> decoder.json.decodeFromJsonElement(TypingStartEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            VOICE_STATE_UPDATE -> {
                val voiceState = decoder.json.decodeFromJsonElement(VoiceState.serializer(), data)
                VoiceStateUpdateEvent(sequenceId, voiceState)
            }

            THREAD_CREATE -> {
                val thread = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ThreadCreateEvent(sequenceId, thread)
            }

            THREAD_UPDATE -> {
                val thread = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ThreadUpdateEvent(sequenceId, thread)
            }

            THREAD_DELETE -> {
                val thread = decoder.json.decodeFromJsonElement(Channel.serializer(), data)
                ThreadDeleteEvent(sequenceId, thread)
            }

            THREAD_LIST_SYNC -> decoder.json.decodeFromJsonElement(ThreadListSyncEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

            THREAD_MEMBER_UPDATE -> {
                val member = decoder.json.decodeFromJsonElement(ThreadMember.serializer(), data)
                val guildId = data.jsonObject["guild_id"]?.jsonPrimitive?.content?.let { Snowflake(it) }
                ThreadMemberUpdateEvent(sequenceId, guildId, member)
            }

            THREAD_MEMBERS_UPDATE -> decoder.json.decodeFromJsonElement(ThreadMembersUpdateEvent.serializer(), data)
                .apply { this.sequenceId = sequenceId }

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

            HBACK -> HBackEvent()

            OPCode.RECONNECT -> ReconnectEvent()

            OPCode.INVALID_SESSION -> {
                val resumable = data?.jsonPrimitive?.boolean ?: false
                InvalidSessionEvent(resumable)
            }

            OPCode.HEARTBEAT -> HeartbeatEvent(0)

            else -> {
                logger.debug { "Unhandled OPCode: $opCode" }
                UnknownEvent(opCode, data)
            }
        }
    }
}