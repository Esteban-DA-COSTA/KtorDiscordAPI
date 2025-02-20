package components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Represent a discord server
 *
 * @property id guild id
 * @property name guild name (2-100 characters, excluding trailing and leading whitespace)
 * @property icon [icon hash](https://discord.com/developers/docs/reference#image-formatting)
 * @property iconHash [icon hash](https://discord.com/developers/docs/reference#image-formatting), returned when in the template object
 * @property splash [splash hash](https://discord.com/developers/docs/reference#image-formatting)
 * @property discoverySplash [discovery splash hash](https://discord.com/developers/docs/reference#image-formatting)); only present for guilds with the "DISCOVERABLE" feature
 * @property owner true if the [User] is the owner of the guild
 * @property ownerId id of owner
 * @property permissions total permissions for the [User] in the guild (excludes overwrites)
 * @property region [voice region](https://discord.com/developers/docs/resources/voice#voice-region-object) id for the guild (deprecated)
 * @property afkChannelId id of afk channel
 * @property afkTimeout afk timeout in seconds, can be set to: 60, 300, 900, 1800, 3600
 * @property widgetEnabled true if the server widget is enabled
 * @property widgetChannelId the channel id that the widget will generate an invite to, or null if set to no invite
 * @property verificationLevel [verification level](https://discord.com/developers/docs/resources/guild#guild-object-verification-level)) required for the guild
 * @property defaultMessageNotifications default [message notifications level](https://discord.com/developers/docs/resources/guild#guild-object-default-message-notification-level)
 * @property explicitContentFilter    [explicit content filter level](https://discord.com/developers/docs/resources/guild#guild-object-explicit-content-filter-level)
 * @property roles roles in the guild
 * @property emojis custom guild emojis
 * @property features enabled guild features
 * @property mfaLevel required [MFA level](https://discord.com/developers/docs/resources/guild#guild-object-mfa-level) for the guild
 * @property applicationId application id of the guild creator if it is bot-created
 * @property systemChannelId the id of the channel where guild notices such as welcome messages and boost events are posted
 * @property systemChannelFlags [(https://discord.com/developers/docs/resources/guild#guild-object-system-channel-flags)]
 * @property rulesChannelId the id of the channel where Community guilds can display rules and/or guidelines
 * @property maxPresences the maximum number of presences for the guild (null is always returned, apart from the largest of guilds)
 * @property maxMembers the maximum number of members for the guild
 * @property vanityUrlCode the vanity url code for the guild
 * @property description the description of a guild
 * @property banner [banner hash](https://discord.com/developers/docs/reference#image-formatting)
 * @property premiumTier [premium tier](https://discord.com/developers/docs/resources/guild#guild-object-premium-tier) (Server Boost level)
 * @property premiumSubscriptionCount the number of boosts this guild currently has
 * @property preferredLocale the preferred [locale](https://discord.com/developers/docs/reference#locales) of a Community guild; used in server discovery and notices from Discord, and sent in interactions; defaults to "en-US"
 * @property publicUpdatesChannelId the id of the channel where admins and moderators of Community guilds receive notices from Discord
 * @property maxVideoChannelUsers the maximum amount of users in a video channel
 * @property approximateMemberCount approximate number of members in this guild, returned from the GET /guilds/<id> endpoint when with_counts is true
 * @property approximatePresenceCount approximate number of non-offline members in this guild, returned from the GET /guilds/<id> endpoint when with_counts is true
 * @property welcomeScreen the welcome screen of a Community guild, shown to new members, returned in an [Invite's](https://discord.com/developers/docs/resources/invite#invite-object guild) object
 * @property nsfwLevel [guild NSFW level](https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level)
 * @property stickers custom guild stickers
 * @property premiumProgressBarEnabled whether the guild has the boost progress bar enabled
 *
 * @see [discord Doc](https://discord.com/developers/docs/resources/guild#guild-object)
 */
@Serializable
data class Guild(
    val id: Long,
    val name: String,
    val icon: String? = null,
    @SerialName("icon_hash") val iconHash: String? = null,
    val splash: String? = null,
    @SerialName("discovery_splash") val discoverySplash: String? = null,
    @SerialName("owner_id") val ownerId: Long? = null,
    val permissions: String? = null,
    val region: String? = null,
    @SerialName("home_header") val homeHeader: String? = null,
    @SerialName("afk_channel_id") val afkChannelId: Long? = null,
    @SerialName("afk_timeout") val afkTimeout: Int,
    @SerialName("widget_enabled") val widgetEnabled: Boolean? = null,
    @SerialName("widget_channel_id") val widgetChannelId: Long? = null,
    @SerialName("verification_level") val verificationLevel: Int,
    @SerialName("default_message_notifications") val defaultMessageNotifications: Int,
    @SerialName("explicit_content_filter") val explicitContentFilter: Int,
    val roles: List<components.Role>,
    val emojis: List<components.Emoji>,
    val features: List<components.GuildFeatures>,
    @SerialName("mfa_level") val mfaLevel: Int,
    @SerialName("application_id") val applicationId: Long? = null,
    @SerialName("system_channel_id") val systemChannelId: Long? = null,
    @SerialName("system_channel_flags") val systemChannelFlags: Int,
    @SerialName("rules_channel_id") val rulesChannelId: Long? = null,
    @SerialName("max_presences") val maxPresences: Int? = null,
    @SerialName("max_members") val maxMembers: Int? = null,
    @SerialName("vanity_url_code") val vanityUrlCode: String? = null,
    val description: String? = null,
    val banner: String? = null,
    @SerialName("premium_tier") val premiumTier: Int,
    @SerialName("premium_subscription_count") val premiumSubscriptionCount: Int? = null,
    @SerialName("preferred_locale") val preferredLocale: String,
    @SerialName("public_updates_channel_id") val publicUpdatesChannelId: Long? = null,
    @SerialName("max_stage_video_channel_users") val maxStageVideoChannelUsers: Long? = null,
    @SerialName("max_video_channel_users") val maxVideoChannelUsers: Int? = null,
    @SerialName("approximate_member_count") val approximateMemberCount: Int? = null,
    @SerialName("approximate_presence_count") val approximatePresenceCount: Int? = null,
    @SerialName("safety_alerts_channel_id") val safetyAlertsChannelId: Long? = null,
    @SerialName("welcome_screen") val welcomeScreen: components.WelcomeScreen? = null,
    @SerialName("nsfw_level") val nsfwLevel: Int,
    @SerialName("hub_type") val hubType: String? = null,
    @SerialName("latest_onboarding_question_id") val latestOnboardingQuestionId: Long? = null,
    val nsfw: Boolean = false,
    val stickers: List<components.Sticker>,
    @SerialName("incidents_data") val incidentsData: String? = null,
    @SerialName("premium_progress_bar_enabled") val premiumProgressBarEnabled: Boolean,
    @SerialName("embed_enabled") val embedEnabled: Boolean?,
    @SerialName("embed_channel_id") val embedChannelId: Long? = null

)

@Serializable
data class WelcomeScreen(
    val description: String? = null,
    @SerialName("welcome_channels") val welcomeChannels: List<components.WelcomeScreenChannel>,
)

@Serializable
data class WelcomeScreenChannel(
    @SerialName("channel_id") val channelId: Long? = null,
    val description: String? = null,
    @SerialName("emoji_id") val emojiId: Long? = null,
    @SerialName("emoji_name") val emojiName: String? = null,
)

@Serializable
enum class GuildFeatures {
    ANIMATED_BANNER,
    ANIMATED_ICON,
    APPLICATION_COMMAND_PERMISSIONS_V2,
    AUTO_MODERATION,
    BANNER,
    COMMUNITY,
    CREATOR_MONETIZABLE_PROVISIONAL,
    CREATOR_STORE_PAGE,
    DEVELOPER_SUPPORT_SERVER,
    DISCOVERABLE,
    FEATURABLE,
    INVITES_DISABLED,
    INVITE_SPLASH,
    MEMBER_VERIFICATION_GATE_ENABLED,
    MORE_STICKERS,
    NEWS,
    PARTNERED,
    PREVIEW_ENABLED,
    ROLE_ICONS,
    ROLE_SUBSCRIPTIONS_AVAILABLE_FOR_PURCHASE,
    ROLE_SUBSCRIPTIONS_ENABLED,
    TICKETED_EVENTS_ENABLED,
    VANITY_URL,
    VERIFIED,
    VIP_REGIONS,
    WELCOME_SCREEN_ENABLED
}

@Serializable
data class UnavailableGuild(
    val id: String,
    val unavailable: Boolean
)