# Catalogue des événements

Liste des événements dispatch que la bibliothèque désérialise et distribue via
[`on<T>()`](Events.md). La colonne **Intent** rappelle l'intent Gateway à demander au
[`login`](Connexion.md#les-intents) pour recevoir l'événement.

> Un événement dont le nom n'est **pas** dans cette liste (non supporté par la lib) est reçu comme
> `UnknownDispatchEvent` plutôt que de faire échouer la connexion. Si tu as besoin d'un événement
> absent, il faut le [câbler](Adv_serialization.md) dans le désérialiseur.
>
{style="note"}

## Connexion &amp; cycle de vie

| Classe | Quand |
|---|---|
| `ReadyEvent` | Handshake terminé, le bot est connecté (porte `sessionId`, `resumeGatewayUrl`) |
| `InteractionCreateEvent` | Une interaction arrive — routée vers le canal des [interactions](Interaction_responses.md), pas vers `on<T>` |

## Messages

| Classe | Intent |
|---|---|
| `MessageCreateEvent` | `GUILD_MESSAGES` / `DIRECT_MESSAGES` (+ `MESSAGE_CONTENT` pour le texte) |
| `MessageUpdateEvent` | idem |
| `MessageDeleteEvent` | idem |
| `MessageDeleteBulkEvent` | idem |

## Réactions

| Classe | Intent |
|---|---|
| `MessageReactionAddEvent` | `GUILD_MESSAGE_REACTIONS` |
| `MessageReactionRemoveEvent` | `GUILD_MESSAGE_REACTIONS` |
| `MessageReactionRemoveAllEvent` | `GUILD_MESSAGE_REACTIONS` |
| `MessageReactionRemoveEmojiEvent` | `GUILD_MESSAGE_REACTIONS` |

## Serveurs, membres &amp; rôles

| Classe | Intent |
|---|---|
| `GuildCreateEvent` / `GuildUpdateEvent` / `GuildDeleteEvent` | `GUILDS` |
| `GuildMemberAddEvent` / `GuildMemberUpdateEvent` / `GuildMemberRemoveEvent` | `GUILD_MEMBERS` (privilégié) |
| `GuildMembersChunkEvent` | réponse à `requestGuildMembers` |
| `GuildRoleCreateEvent` / `GuildRoleUpdateEvent` / `GuildRoleDeleteEvent` | `GUILDS` |

## Salons &amp; threads

| Classe | Intent |
|---|---|
| `ChannelCreateEvent` / `ChannelUpdateEvent` / `ChannelDeleteEvent` | `GUILDS` |
| `ThreadCreateEvent` / `ThreadUpdateEvent` / `ThreadDeleteEvent` | `GUILDS` |
| `ThreadListSyncEvent` | `GUILDS` |
| `ThreadMemberUpdateEvent` / `ThreadMembersUpdateEvent` | `GUILDS` / `GUILD_MEMBERS` |

## Présence, voix &amp; frappe

| Classe | Intent |
|---|---|
| `PresenceUpdateEvent` | `GUILD_PRESENCES` (privilégié) |
| `VoiceStateUpdateEvent` | `GUILD_VOICE_STATES` |
| `TypingStartEvent` | `GUILD_MESSAGE_TYPING` |
