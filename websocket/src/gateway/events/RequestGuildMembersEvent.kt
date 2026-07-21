package ktordiscord.gateway.events

import ktordiscord.components.Snowflake
import ktordiscord.gateway.OPCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Payload `d` of a Request Guild Members command (OP 8).
 *
 * Either [query] + [limit] (an empty [query] with [limit] 0 requests every member) or [userIds] must
 * be provided. Requires the `GUILD_MEMBERS` privileged intent. Discord answers with one or more
 * inbound `GUILD_MEMBERS_CHUNK` dispatch events (not yet modeled — see IMPROVEMENTS.md).
 *
 * @see [Request Guild Members](https://discord.com/developers/docs/events/gateway-events#request-guild-members)
 */
@Serializable
data class RequestGuildMembersData(
    @SerialName("guild_id") val guildId: Snowflake,
    val query: String? = "",
    val limit: Int = 0,
    val presences: Boolean? = null,
    @SerialName("user_ids") val userIds: List<Snowflake>? = null,
    val nonce: String? = null
)

/**
 * Outgoing Request Guild Members command (OP 8): requests (and chunks) a guild's member list.
 */
@Serializable(RequestGuildMembersSerializer::class)
class RequestGuildMembersEvent(data: RequestGuildMembersData) : Event() {

    @SerialName("d")
    val data = data
}

object RequestGuildMembersSerializer : OutgoingEventSerializer<RequestGuildMembersEvent, RequestGuildMembersData>(
    "requestGuildMembers", OPCode.REQUEST_GUILD_MEMBERS, RequestGuildMembersData.serializer(), { it.data }
)
