package ktordiscord.gateway.events

import ktordiscord.components.Snowflake
import ktordiscord.components.enums.ChannelTypes
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Decoding tests for the extended Gateway dispatch coverage: every newly typed event must decode
 * into its concrete class (not [UnknownDispatchEvent]), carry its [DispatchEvent.sequenceId] from the
 * envelope, and expose its key fields as [Snowflake] where applicable.
 */
class DispatchCoverageTest {
    // Mirrors the WebSocket Json config in DiscordClient.
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    private inline fun <reified T> decode(t: String, s: Int, d: String): T {
        val event = json.decodeFromString(Event.serializer(), """{"op":0,"t":"$t","s":$s,"d":$d}""")
        return assertIs<T>(event)
    }

    @Test
    fun guildUpdateDecodes() {
        val event = decode<GuildUpdateEvent>(
            "GUILD_UPDATE", 1,
            """{"id":10,"name":"My Guild","afk_timeout":300,"verification_level":1,"default_message_notifications":0,"explicit_content_filter":0,"roles":[],"emojis":[],"features":[],"mfa_level":0,"system_channel_flags":0,"premium_tier":0,"preferred_locale":"en-US","nsfw_level":0,"stickers":[],"premium_progress_bar_enabled":false}"""
        )
        assertEquals(1, event.sequenceId)
        assertEquals("My Guild", event.guild.name)
    }

    @Test
    fun guildDeleteDecodes() {
        val event = decode<GuildDeleteEvent>("GUILD_DELETE", 2, """{"id":"10","unavailable":true}""")
        assertEquals(2, event.sequenceId)
        assertTrue(event.guild.unavailable)
    }

    @Test
    fun guildMemberAddDecodes() {
        val event = decode<GuildMemberAddEvent>(
            "GUILD_MEMBER_ADD", 3,
            """{"guild_id":"10","nick":"Bob","user":{"id":42}}"""
        )
        assertEquals(3, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals("Bob", event.member.nick)
        assertEquals(42L, event.member.user?.id)
    }

    @Test
    fun guildMemberUpdateDecodes() {
        val event = decode<GuildMemberUpdateEvent>(
            "GUILD_MEMBER_UPDATE", 4,
            """{"guild_id":"10","roles":["1","2"],"user":{"id":42},"nick":"Bobby"}"""
        )
        assertEquals(4, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals(listOf(Snowflake("1"), Snowflake("2")), event.roles)
        assertEquals(42L, event.user.id)
    }

    @Test
    fun guildMemberRemoveDecodes() {
        val event = decode<GuildMemberRemoveEvent>(
            "GUILD_MEMBER_REMOVE", 5,
            """{"guild_id":"10","user":{"id":42}}"""
        )
        assertEquals(5, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals(42L, event.user.id)
    }

    @Test
    fun guildMembersChunkDecodes() {
        val event = decode<GuildMembersChunkEvent>(
            "GUILD_MEMBERS_CHUNK", 6,
            """{"guild_id":"10","members":[{"user":{"id":42}}],"chunk_index":0,"chunk_count":1}"""
        )
        assertEquals(6, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals(1, event.members.size)
        assertEquals(1, event.chunkCount)
    }

    @Test
    fun guildRoleCreateDecodes() {
        val event = decode<GuildRoleCreateEvent>(
            "GUILD_ROLE_CREATE", 7,
            """{"guild_id":"10","role":{"id":99,"name":"Admin","description":null,"color":0,"hoist":false,"icon":null,"unicode_emoji":null,"position":1,"permissions":"0","managed":false,"mentionable":true}}"""
        )
        assertEquals(7, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals("Admin", event.role.name)
    }

    @Test
    fun guildRoleDeleteDecodes() {
        val event = decode<GuildRoleDeleteEvent>(
            "GUILD_ROLE_DELETE", 8,
            """{"guild_id":"10","role_id":"99"}"""
        )
        assertEquals(8, event.sequenceId)
        assertEquals(Snowflake("99"), event.roleId)
    }

    @Test
    fun messageDeleteBulkDecodes() {
        val event = decode<MessageDeleteBulkEvent>(
            "MESSAGE_DELETE_BULK", 9,
            """{"ids":["1","2","3"],"channel_id":"55","guild_id":"10"}"""
        )
        assertEquals(9, event.sequenceId)
        assertEquals(listOf(Snowflake("1"), Snowflake("2"), Snowflake("3")), event.ids)
        assertEquals(Snowflake("55"), event.channelId)
    }

    @Test
    fun messageReactionAddDecodes() {
        val event = decode<MessageReactionAddEvent>(
            "MESSAGE_REACTION_ADD", 10,
            """{"user_id":"42","channel_id":"55","message_id":"77","guild_id":"10","emoji":{"name":"👍"}}"""
        )
        assertEquals(10, event.sequenceId)
        assertEquals(Snowflake("42"), event.userId)
        assertEquals(Snowflake("77"), event.messageId)
        assertEquals("👍", event.emoji.name)
    }

    @Test
    fun messageReactionRemoveEmojiDecodes() {
        val event = decode<MessageReactionRemoveEmojiEvent>(
            "MESSAGE_REACTION_REMOVE_EMOJI", 11,
            """{"channel_id":"55","message_id":"77","emoji":{"name":"👍"}}"""
        )
        assertEquals(11, event.sequenceId)
        assertNull(event.guildId)
        assertEquals("👍", event.emoji.name)
    }

    @Test
    fun typingStartDecodes() {
        val event = decode<TypingStartEvent>(
            "TYPING_START", 12,
            """{"channel_id":"55","user_id":"42","timestamp":1700000000}"""
        )
        assertEquals(12, event.sequenceId)
        assertEquals(Snowflake("55"), event.channelId)
        assertEquals(1700000000L, event.timestamp)
        assertNull(event.member)
    }

    @Test
    fun voiceStateUpdateOnLeaveHasNullChannel() {
        val event = decode<VoiceStateUpdateEvent>(
            "VOICE_STATE_UPDATE", 13,
            """{"guild_id":"10","channel_id":null,"user_id":"42","session_id":"abc","self_mute":true}"""
        )
        assertEquals(13, event.sequenceId)
        assertNull(event.voiceState.channelId)
        assertEquals(Snowflake("42"), event.voiceState.userId)
        assertTrue(event.voiceState.selfMute)
    }

    @Test
    fun threadCreateDecodesAsChannel() {
        val event = decode<ThreadCreateEvent>(
            "THREAD_CREATE", 14,
            """{"id":"88","type":11,"guild_id":"10","parent_id":"55","name":"my-thread","thread_metadata":{"archived":false,"auto_archive_duration":1440}}"""
        )
        assertEquals(14, event.sequenceId)
        assertEquals(ChannelTypes.PUBLIC_THREAD, event.thread.type)
        assertEquals(Snowflake("55"), event.thread.parentId)
        assertEquals(1440, event.thread.threadMetadata?.autoArchiveDuration)
    }

    @Test
    fun threadMemberUpdateExtractsGuildId() {
        val event = decode<ThreadMemberUpdateEvent>(
            "THREAD_MEMBER_UPDATE", 15,
            """{"id":"88","user_id":"42","join_timestamp":"2024-01-01T00:00:00Z","flags":0,"guild_id":"10"}"""
        )
        assertEquals(15, event.sequenceId)
        assertEquals(Snowflake("10"), event.guildId)
        assertEquals(Snowflake("42"), event.member.userId)
    }

    @Test
    fun threadMembersUpdateDecodes() {
        val event = decode<ThreadMembersUpdateEvent>(
            "THREAD_MEMBERS_UPDATE", 16,
            """{"id":"88","guild_id":"10","member_count":3,"removed_member_ids":["42"]}"""
        )
        assertEquals(16, event.sequenceId)
        assertEquals(3, event.memberCount)
        assertEquals(listOf(Snowflake("42")), event.removedMemberIds)
    }
}
