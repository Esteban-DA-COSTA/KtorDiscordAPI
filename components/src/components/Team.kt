package ktordiscord.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Snowflake,
    val icon: String?,
    val members: TeamMember,
    val name: String,
    @SerialName("owner_user_id") val ownerUserId: Snowflake
)

@Serializable
data class TeamMember(
    @SerialName("membership_state") val membershipState: Int,
    val permissions: List<String>,
    @SerialName("team_id") val teamId: Snowflake,
    val user: User
)
