# Améliorations identifiées

Dette technique et pistes d'amélioration relevées lors de l'audit du 2026-07-17. Cocher au fur et à mesure ; supprimer une entrée si elle devient sans objet.

## Roadmap v1.0 beta

Priorisation (2026-07-21) de la dette résiduelle restante en vue d'une publication **v1.0 beta**. Les items ci-dessous référencent les entrées détaillées des sections plus bas. Principe directeur : tout ce qui touche le **contrat public** (types, signatures) doit être figé **avant** d'exposer des early adopters, sinon cela devient un breaking change.

### Tier 1 — Bloquant : figer l'API publique

À traiter **avant** la beta (breaking change sinon).

- [x] **Harmoniser le typage des ids sur `Snowflake` partout.** Cf. *« IDs `Long`/`String` dans les modèles »* et *« Incohérence de typage des ids »*. *Fait (2026-07-21) : tous les ids des modèles (`Guild`/`Role`/`User`/`Member`/`Emoji`/`Application`/`Team`/`Sticker`/`Activity`/`Overwrite`/`Message.channelId`…), des payloads sortants et des ~60 signatures REST (incluant la pagination `before`/`after`/`around`) passent en `Snowflake`. Sites internes repointés (`applicationId`, `interaction.id`…), `.value` supprimés. Non convertis (identifiants non-snowflake) : tokens, `custom_id`, `emoji`, `session_id`, `ActivityParty.id`. Bitfields de permissions laissés en `String`. `applicationId` : sentinelle `Snowflake("")` (inline class ⇒ pas de `lateinit`).*
- [x] **Compléter les modèles `Message` et `Member` sur les champs courants.** Cf. *« Modèles `Member`/`Message` partiels »*. *Fait (2026-07-21) : `Message` gagne `mention_everyone`, `mentions`, `mention_roles`, `reactions`, `pinned`, `webhook_id`, `type` (nouvel enum `MessageType`), `attachments`, `message_reference` (nouveau modèle `MessageReference`), `referenced_message`, `sticker_items` (nouveau modèle `StickerItem`), `flags`. `Member` gagne `banner`, `premium_since`, `pending`, `flags`, `communication_disabled_until`. `Attachment` rendu tolérant (champs optionnels nullables/défauts). **Reporté (Tier 3)** : `components` en réception (sérialiseur `MessageComponent` write-only) et `nonce` (type mixte int/string → risque de crash de décodage).*
- [x] **Enum `ComponentType` au lieu de `Int`.** Cf. *« `MessageComponentData.componentType` reste un `Int` »*. *Fait (2026-07-21) : enum `ComponentType` (pattern `IntEnumSerializer`, `UNKNOWN(-1)` de repli) sur `MessageComponentData.componentType`.*

### Tier 2 — Fortement recommandé

- [ ] **Éviction des `componentHandlers`.** Cf. *« Callbacks de composants en mémoire »*. La map croît sans limite → **fuite mémoire sur tout bot long-running**. Registre borné, TTL ou `customId` stable.
- [ ] **Packaging release** (hors dette listée ci-dessous, prérequis de publication) :
  - [ ] **Versioning** : aucun numéro de version n'existe (`project.yaml` / `module.yaml`). Définir `1.0.0-beta`.
  - [ ] **Publication** : rien de configuré. **JitPack** = chemin le plus rapide pour une beta (tag Git, zéro config Maven Central). `core` est déjà une façade `exported` correcte.
  - [ ] **Documentation d'entrée** : pas de `README.md` racine ; `Writerside/topics/Core_module.md` vide et Home en placeholder « My app ». Les guides Basic_usage / Interactions / Events sont déjà corrects.

### Tier 3 — Assumable en « known limitations » (post-beta)

À **documenter** honnêtement plutôt qu'à implémenter pour la beta :

- **Voice State Update (OP 4) sortant** (support vocal) — cf. section « Commandes Gateway sortantes ».
- **Select menus / modals** — cf. *« Composants limités aux boutons »* (+ *« Sérialiseur `MessageComponent` write-only »*).
- **Persistance des handlers au redémarrage**, **catch-all events**, **ordre des multi-handlers** — cf. sections « Routeur d'events » / « Routeur d'interactions ».
- **Modèles entrants REST encore partiels** (`permission_overwrites`, `global_name`, `Emoji`…) — cf. section « Couverture des endpoints REST ».

### Ordre de bataille

1. ~~`Snowflake` partout (modèles + signatures REST)~~ ✅ fait (2026-07-21)
2. ~~Compléter `Message` + `Member`~~ ✅ fait (2026-07-21)
3. ~~Enum `ComponentType`~~ ✅ fait (2026-07-21)
4. Éviction `componentHandlers`
5. Version + JitPack + README + `Core_module.md`
6. Rédiger la section « Known limitations » (Tier 3)

**Tier 1 livré (2026-07-21).** L'API publique est figée côté types d'ids, modèles centraux et enum de
composant. Reste avant la beta : le Tier 2 (fuite `componentHandlers` + packaging/doc).

> Note : le piège « crash en DM sur `MESSAGE_CREATE`/`MESSAGE_UPDATE` » qui figurait dans `CLAUDE.md` était
> déjà **obsolète** (décodage nullable dans `Event.kt`, corrigé via le point 3) — `CLAUDE.md` a été mis à jour.

## Priorité 1 — Robustesse face à l'API Discord

- [x] **1. Tolérance aux événements/opcodes inconnus.** `DispatchEvents.valueOf(eventName)` (`websocket/src/gateway/events/Event.kt`) lève une exception sur tout événement dispatch non listé (`GUILD_MEMBER_UPDATE`, `TYPING_START`…), `OPCodeSerializer` fait de même pour un opcode inconnu, et `InteractionData.Serializer` a un `TODO(type!!.name)` pour tous les types sauf `APPLICATION_COMMAND`. Chaque occurrence plante la boucle de session et force une reconnexion. → Introduire un fallback `UnknownEvent(name, data)` loggé en debug au lieu de lever. Meilleur ratio effort/bénéfice du projet.

- [x] **2. Sérialiseurs d'enums entiers : lookup par id + factorisation.** `InteractionTypes.Serializer` fait `entries[decodeInt() - 1]` (fragile, dépend de la contiguïté des ids). → Rechercher par id comme `OPCodeSerializer`, et factoriser le pattern copié-collé dans une classe de base réutilisable type `IntEnumSerializer` avec valeur `UNKNOWN` de repli (Discord ajoute régulièrement des valeurs).

- [x] **3. Bugs de décodage `MESSAGE_CREATE`/`MESSAGE_UPDATE`** (`EventSerializer.decodeDispatchEvent`) :
  - `data.jsonObject["guild_id"]?.jsonPrimitive?.toString()` retourne la représentation JSON **avec guillemets** (`"\"123\""`) au lieu de la valeur → utiliser `.content`. Toute comparaison sur `guildId` est silencieusement fausse aujourd'hui.
  - `member!!` et `mentions!!` : NPE pour les messages hors guilde (DM) → rendre le décodage nullable.

## Priorité 2 — Design de la bibliothèque

- [x] **4. Supprimer le `runBlocking` de l'`init` de `DiscordClient`.** Constructeur bloquant, non testable, qui exige réseau + token valide. → Factory suspend (`DiscordClient.create(token)`) ou récupération paresseuse de `applicationId` au `login()`. *Fait : constructeur privé + `suspend DiscordClient.create()` ; `InteractionManager.create()` suspend (second `runBlocking` supprimé) et ordre d'init de `appCommands` corrigé.*

- [x] **5. Channels rendezvous → bufferisés (ou `SharedFlow`).** `Channel<DispatchEvent>()` sans buffer : si le consommateur est lent ou absent (bot qui ne lit que `interactions`), la boucle Gateway se suspend sur `send` et ne traite plus aucune frame. → `Channel(BUFFERED)` au minimum ; `SharedFlow` pour permettre plusieurs consommateurs. *Fait : `MutableSharedFlow` (buffer 64, `DROP_OLDEST`) exposé en `SharedFlow` read-only ; consommateurs via `collect { }` lancés avant `login()`.*

- [x] **6. Concurrence structurée dans `DiscordWebSocketSession`.** Les `CoroutineScope(Dispatchers.Default).launch` ad hoc (connexion, heartbeat) ne sont rattachés à rien : impossible d'arrêter proprement le client. → Scope porté par `DiscordClient` + méthode `close()`. *Fait : `CoroutineScope(SupervisorJob() + Dispatchers.Default)` porté par `DiscordClient`, injecté dans la session ; `DiscordClient.close()` annule le scope + ferme le `HttpClient`.*

- [x] **7. Séparer payloads sortants et modèles entrants.** `Message` sert à la fois de DTO d'envoi (DSL `sendMessage { }`) et de modèle reçu (`MESSAGE_CREATE`), d'où les `var` nullables partout. Le bon pattern existe déjà avec `RolePayload` → le généraliser (payloads mutables pour l'envoi, modèles immuables `val` en réception). Inclut le `Snowflake("-1")` factice de `createGlobalApplicationCommand` → créer un `ApplicationCommandPayload` sans `id`. *Fait : `MessagePayload` (envoi) vs `Message` immuable (réception) ; `ApplicationCommandPayload` sans `id` (`Snowflake("-1")` supprimé). `Embed` + sous-objets restent mutables partagés (décision assumée).*

- [x] **8. Gestion du rate limiting REST (429).** Les appels retournent le `HttpResponse` brut sans détection ni retry. → Intercepteur Ktor minimal : lire `Retry-After`, attendre, rejouer. *Fait : plugin `HttpRequestRetry` configuré une fois sur le `HttpClient` (`DiscordClient.kt`), `maxRetries = 5`, retry sur `429`, délai via `Retry-After` puis `X-RateLimit-Reset-After` (cap 60 s). Toutes les requêtes REST en héritent transparemment.*

## Priorité 3 — Hygiène / cosmétique

- [x] **9. Adopter une racine de package commune.** Les fichiers de `core` sont dans le package par défaut (inimportable depuis Java, risque de collisions). → `ktordiscord.core`, `ktordiscord.gateway`, etc. Refactor mécanique, à faire tôt tant qu'il n'y a pas de consommateurs externes. *Fait : racine `ktordiscord.*` sur tous les modules (`ktordiscord.core`, `ktordiscord.gateway[.events]`, `ktordiscord.components[.enums|.interactions|.serialization]`, `ktordiscord.builders`, `ktordiscord.app`). Point d'entrée `ktordiscord.app.MainKt` auto-détecté par Amper (pas de `mainClass` à pinner).*

- [x] **10. Dégraisser le DSL `DiscordMessageBuilder.kt`.** Les fonctions type `Embed.title(title)` doublonnent les propriétés `var` assignables dans le lambda. Bug au passage : `EmbedAuthor.iconUrl(url)` assigne `this.url` au lieu de `this.iconUrl`. → Garder les propriétés ; ne conserver que les fonctions à valeur ajoutée (`embed { }`, `field { }`, `color(Color)`). *Fait : 14 fonctions redondantes supprimées (0 site d'appel), conservé `embed`/`footer`/`image`/`thumbnail`/`video`/`provider`/`author`/`field` + `color(Color)`. Bug `EmbedAuthor.iconUrl` supprimé de fait. Bonus : corrigé `Main.kt` (`content =` → `description =` dans un bloc `embed { }`, qui visait en réalité `MessagePayload.content`). Note : le `@DslMarker` reste inopérant (posé sur les fonctions, pas les types récepteurs) — piste séparée.* **Suivi (traité) :** marqueur unifié et renommé `@DiscordDsl` (`@Target(AnnotationTarget.CLASS)`, isolé dans `DiscordDsl.kt` ; `DiscordMessageBuilder.kt` → `MessageBuilders.kt`), déplacé sur **les classes réceptrices** et étendu à tout le DSL (contenu + scopes `ResponseScope`/`CommandScope`/`InteractionScope`/`EventScope` + `ApplicationCommandPayload`). Groupe unique → plus aucune fuite de scope entre lambdas imbriqués.

- [x] **11. Ajouter des tests unitaires de désérialisation.** `EventSerializer`, les enums et `Snowflake` sont des fonctions pures, testables sans réseau avec des payloads JSON réels de Discord. Verrouille les points 1 à 3. Rappel : le projet n'a actuellement **aucun test** (`./kotlin test`). *Fait : `EventDecodeTest` (déjà présent, points 1-3) complété par `SnowflakeTest` + `IntEnumSerializerTest` (`components/test/`) et `OPCodeTest` (`websocket/test/`). 15 tests, tous verts via `./kotlin test`. Répertoire `components/test/` auto-détecté sans config.*

## Routeur d'interactions & boutons (v1 livrée)

Système `discordClient.on("cmd") { respond { button(...).click { } } }` : routeur commande→handler + boutons chaînables (composants de message, dispatch `MESSAGE_COMPONENT`, `defer`/`editOriginal`/`update`, `ephemeral`). Voir `core/src/interactions/`, `components/src/components/MessageComponent.kt`. Dette résiduelle :

- [ ] **Callbacks de composants en mémoire.** `componentHandlers` (custom_id → callback) vit dans `DiscordClient` : perdu au redémarrage, et **croît sans éviction** (chaque rendu d'un bouton auto-`custom_id` ajoute une entrée). → customId stable optionnel, TTL/éviction, ou registre borné.
- [ ] **Composants limités aux boutons.** Modèle `MessageComponent` scellé prêt à étendre → select menus (`type 3`), text inputs / modals (`MODAL` + `MODAL_SUBMIT`).
- [x] **`MessageComponentData.componentType` reste un `Int`.** → enum `ComponentType` sérialisé par entier (pattern `IntEnumSerializer`). *Fait (2026-07-21, Tier 1) : enum `ComponentType` (`ACTION_ROW`…`CHANNEL_SELECT` + `UNKNOWN(-1)`).*
- [x] **Pas de bloc `define { }`.** L'ergonomie `respond { }` a été retenue pour permettre plus tard un bloc `define { }` créant l'`ApplicationCommand` dans la même lambda `on`. *Fait : `on(name) { }` reçoit désormais un `CommandScope` (config, exécuté à l'enregistrement) avec `define(guildId?) { }` (global ou guilde), `handle { }` (dispatch dynamique complet), et les sucres `respond { }` / `defer()`. Les `define` sont collectés puis synchronisés en **bulk-overwrite** au `login()`. Endpoints REST Application Command complétés au passage (global get/edit/delete/bulk, guild create/get/list/edit/delete/bulk, permissions get/edit) + modèles de permissions.*
- [ ] **Sérialiseur `MessageComponent` write-only.** `deserialize` lève une erreur (on n'émet que des composants). À compléter si un jour on doit décoder des composants entrants complets.

## Routeur d'events Gateway (v1 livrée)

`discordClient.on<MessageCreateEvent> { reply { } }` : routeur réifié event→handler(s), multi-listeners, boucle de dispatch interne symétrique de celle des interactions, scope `EventScope<T>` avec `reply { }` réutilisant `ResponseScope` (boutons `.click` gratuits sur un message d'event). Voir `core/src/events/EventScope.kt`, `core/src/DiscordClient.kt`. Dette résiduelle :

- [ ] **Handlers d'events en mémoire.** `eventHandlers` (classe d'event → handlers) vit dans `DiscordClient` : perdu au redémarrage. Pas de désenregistrement (`off`).
- [ ] **Match par classe exacte.** Le routage matche `event::class` exactement : pas de handler « catch-all » (sur `DispatchEvent`) ni de dispatch par hiérarchie. À étendre si besoin.
- [ ] **`reply` limité aux events porteurs d'un channel.** Fourni pour `MessageCreateEvent`/`MessageUpdateEvent` (extensions typées, `@JvmName` pour l'erasure). Les autres events accèdent au client via `event` + les fonctions REST existantes.
- [ ] **Multi-handlers non ordonnancés.** Chaque handler d'un même type tourne dans son propre coroutine : pas de garantie d'ordre ni de « stop propagation ».

## Commandes Gateway sortantes (v1 livrée)

Jusqu'ici seuls Identify (OP 2) / Resume (OP 6) / Heartbeat (OP 1) étaient émis. Ajout de l'envoi de
**Presence Update (OP 3)** et **Request Guild Members (OP 8)**, exposés sur `DiscordClient`
(`updatePresence { }`, `requestGuildMembers(guildId) { }`). Factorisation de l'enveloppe `{op, d}`
dans `OutgoingEventSerializer` (base réutilisable, sur le modèle d'`IntEnumSerializer` ; Identify /
Resume repointés dessus). Nouveau modèle sortant léger `BotActivity` + enum `ActivityType` ; `Presence`
repointé sur `BotActivity` et `status` typé `StatusTypeEnum`. Helper d'envoi générique `sendGateway`
(reified, garde l'état de connexion) dans `DiscordWebSocketSession`. Voir
`websocket/src/gateway/events/{OutgoingEventSerializer,UpdatePresenceEvent,RequestGuildMembersEvent}.kt`,
`core/src/gateway/OutgoingCommandScopes.kt`. Dette résiduelle :

- [ ] **Voice State Update (OP 4) sortant.** Non implémenté. → event sortant + payload
  (`guild_id`, `channel_id?`, `self_mute`, `self_deaf`) sur la base `OutgoingEventSerializer`, et
  méthode publique sur `DiscordClient` (rejoindre/quitter un salon vocal).
- [x] **Réception des `GUILD_MEMBERS_CHUNK`.** La commande OP 8 est envoyée mais la réponse n'est pas
  décodée : ajouter `GUILD_MEMBERS_CHUNK` dans `DispatchEvents`, un modèle `GuildMembersChunk`, et la
  branche correspondante dans `EventSerializer.decodeDispatchEvent` (`Event.kt`) pour rendre
  `requestGuildMembers` utile de bout en bout. *Fait dans le lot « Couverture des événements dispatch »
  ci-dessous (`GuildMembersChunkEvent`).*

## Couverture des événements Gateway dispatch (v1 élargie)

Le décodage ne typait que 10 événements dispatch ; tout le reste retombait sur
`UnknownDispatchEvent`. Ajout d'un lot complet, tous routables via `discordClient.on<XxxEvent> { }` :
guild lifecycle (`GUILD_UPDATE/DELETE`), membres (`GUILD_MEMBER_ADD/UPDATE/REMOVE`,
`GUILD_MEMBERS_CHUNK`), rôles (`GUILD_ROLE_CREATE/UPDATE/DELETE`), `MESSAGE_DELETE_BULK`,
réactions (`MESSAGE_REACTION_ADD/REMOVE/REMOVE_ALL/REMOVE_EMOJI`), `TYPING_START`,
`VOICE_STATE_UPDATE` (nouveau modèle `VoiceState`) et threads
(`THREAD_CREATE/UPDATE/DELETE/LIST_SYNC/MEMBER_UPDATE/MEMBERS_UPDATE`, avec enrichissement de
`Channel` + `ThreadMetadata`/`ThreadMember`). Décision transverse : **tous les IDs de la couche
event passent en `Snowflake`** — refactor inclus des wrappers existants (`MessageCreate/Update/Delete`).
Nouveaux fichiers `websocket/src/gateway/events/{Guild,Member,Role,Reaction,Typing,Voice,Thread}Events.kt`,
modèle `components/src/components/VoiceState.kt`. Tests : `DispatchCoverageTest` (un décodage par event).
Dette résiduelle :

- [x] **Modèles `Member`/`Message` partiels.** *Fait (2026-07-21, Tier 1) : `Message` et `Member` enrichis
  des champs courants (`attachments`, `reactions`, `type`, `pinned`, `premium_since`, `pending`,
  `communication_disabled_until`…). Reliquat assumé : `components` en réception (sérialiseur write-only)
  et `nonce` (type mixte) laissés de côté, cf. Tier 3.*
- [x] **IDs `Long`/`String` dans les modèles.** *Fait (2026-07-21, Tier 1) : `Guild`/`Role`/`User`/`Member`
  et les autres modèles passent tous en `Snowflake` (voir Tier 1). Plus d'incohérence de couche.*

## Couverture des endpoints REST (v1 élargie)

Extension large de `core/src/rest/` par ressource Discord (retour `HttpResponse` brut, statu quo) :
Channels (get/modify/delete, get/list/edit/delete message, bulk-delete, pins, typing), Reactions
(add/delete own/delete user/list/delete all/delete all-for-emoji), Guilds (get/create channels,
edit/delete/reorder roles), Members (list/get/add/modify/remove), Bans (list/get/create/remove),
Emojis (list/get/create/modify/delete), Users (get @me/get user/create DM). Nouveaux payloads
sortants (`ModifyChannelPayload`, `CreateChannelPayload`, `BulkDeleteMessagesPayload`,
`RolePositionPayload`, `AddMemberPayload`, `ModifyMemberPayload`, `CreateBanPayload`,
`CreateEmojiPayload`, `ModifyEmojiPayload`, `CreateDMPayload`) et modèles entrants (`Overwrite`,
`Reaction`, `Ban`). Nouveaux fichiers `rest/{Reaction,Member,Ban,Emoji,User}Requests.kt`. Tests
d'encodage/décodage : `RestPayloadEncodingTest`, `RestModelDecodingTest`. Dette résiduelle :

- [x] **Réponses REST typées.** Toutes les fonctions REST renvoient désormais un
  `DiscordResponse<T>` scellé (`Success(value, status)` / `Failure(status, error)`) au lieu de
  `HttpResponse` brut : décodage typé du corps (objet / liste / `Unit` sur 204), gestion
  centralisée des 4xx/5xx avec corps d'erreur `DiscordError` typé, et helpers
  `getOrNull` / `getOrThrow` / `onSuccess` / `onFailure`. Voir `rest/DiscordResponse.kt`,
  `components/DiscordError.kt`. Tests : `DiscordResponseTest`, `DiscordErrorDecodingTest`.
- [ ] **Modèles entrants encore partiels.** `Member` est désormais complété (`premium_since`, `flags`,
  `pending`, `communication_disabled_until`… — Tier 1). Restent incomplets : `permission_overwrites` sur
  `Channel`, `global_name` sur `User`, `Emoji` basique. À compléter au besoin.
- [x] **Incohérence de typage des ids.** *Fait (2026-07-21, Tier 1) : `Snowflake` généralisé à tous les
  modèles et à tous les paramètres d'id des fonctions REST (pagination incluse). Restent en `String`/`Int`
  les non-ids (tokens, `custom_id`, bitfields de permissions, `session_id`, `ActivityParty.id`).*

## Divers (déjà noté dans CLAUDE.md, rappelé ici)

- [x] `MessageUpdateEvent` (`MessageEvents.kt`) n'a pas l'annotation `@Serializable`.
