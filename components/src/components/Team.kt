package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Long,
    val icon: String?,
    val members: ktordiscord.components.TeamMember,
    val name: String,
    @SerialName("owner_user_id") val ownerUserId: Long
)

@Serializable
data class TeamMember(
    @SerialName("membership_state") val membershipState: Int,
    val permissions: List<String>,
    @SerialName("team_id") val teamId: Long,
    val user: ktordiscord.components.User
)
