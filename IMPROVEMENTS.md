# Améliorations identifiées

Dette technique et pistes d'amélioration relevées lors de l'audit du 2026-07-17. Cocher au fur et à mesure ; supprimer une entrée si elle devient sans objet.

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

- [ ] **8. Gestion du rate limiting REST (429).** Les appels retournent le `HttpResponse` brut sans détection ni retry. → Intercepteur Ktor minimal : lire `Retry-After`, attendre, rejouer.

## Priorité 3 — Hygiène / cosmétique

- [x] **9. Adopter une racine de package commune.** Les fichiers de `core` sont dans le package par défaut (inimportable depuis Java, risque de collisions). → `ktordiscord.core`, `ktordiscord.gateway`, etc. Refactor mécanique, à faire tôt tant qu'il n'y a pas de consommateurs externes. *Fait : racine `ktordiscord.*` sur tous les modules (`ktordiscord.core`, `ktordiscord.gateway[.events]`, `ktordiscord.components[.enums|.interactions|.serialization]`, `ktordiscord.builders`, `ktordiscord.app`). Point d'entrée `ktordiscord.app.MainKt` auto-détecté par Amper (pas de `mainClass` à pinner).*

- [x] **10. Dégraisser le DSL `DiscordMessageBuilder.kt`.** Les fonctions type `Embed.title(title)` doublonnent les propriétés `var` assignables dans le lambda. Bug au passage : `EmbedAuthor.iconUrl(url)` assigne `this.url` au lieu de `this.iconUrl`. → Garder les propriétés ; ne conserver que les fonctions à valeur ajoutée (`embed { }`, `field { }`, `color(Color)`). *Fait : 14 fonctions redondantes supprimées (0 site d'appel), conservé `embed`/`footer`/`image`/`thumbnail`/`video`/`provider`/`author`/`field` + `color(Color)`. Bug `EmbedAuthor.iconUrl` supprimé de fait. Bonus : corrigé `Main.kt` (`content =` → `description =` dans un bloc `embed { }`, qui visait en réalité `MessagePayload.content`). Note : le `@DslMarker` reste inopérant (posé sur les fonctions, pas les types récepteurs) — piste séparée.*

- [x] **11. Ajouter des tests unitaires de désérialisation.** `EventSerializer`, les enums et `Snowflake` sont des fonctions pures, testables sans réseau avec des payloads JSON réels de Discord. Verrouille les points 1 à 3. Rappel : le projet n'a actuellement **aucun test** (`./kotlin test`). *Fait : `EventDecodeTest` (déjà présent, points 1-3) complété par `SnowflakeTest` + `IntEnumSerializerTest` (`components/test/`) et `OPCodeTest` (`websocket/test/`). 15 tests, tous verts via `./kotlin test`. Répertoire `components/test/` auto-détecté sans config.*

## Routeur d'interactions & boutons (v1 livrée)

Système `discordClient.on("cmd") { respond { button(...).click { } } }` : routeur commande→handler + boutons chaînables (composants de message, dispatch `MESSAGE_COMPONENT`, `defer`/`editOriginal`/`update`, `ephemeral`). Voir `core/src/interactions/`, `components/src/components/MessageComponent.kt`. Dette résiduelle :

- [ ] **Callbacks de composants en mémoire.** `componentHandlers` (custom_id → callback) vit dans `DiscordClient` : perdu au redémarrage, et **croît sans éviction** (chaque rendu d'un bouton auto-`custom_id` ajoute une entrée). → customId stable optionnel, TTL/éviction, ou registre borné.
- [ ] **Composants limités aux boutons.** Modèle `MessageComponent` scellé prêt à étendre → select menus (`type 3`), text inputs / modals (`MODAL` + `MODAL_SUBMIT`).
- [ ] **`MessageComponentData.componentType` reste un `Int`.** → enum `ComponentType` sérialisé par entier (pattern `IntEnumSerializer`).
- [ ] **Pas de bloc `define { }`.** L'ergonomie `respond { }` a été retenue pour permettre plus tard un bloc `define { }` créant l'`ApplicationCommand` dans la même lambda `on`. À implémenter.
- [ ] **Sérialiseur `MessageComponent` write-only.** `deserialize` lève une erreur (on n'émet que des composants). À compléter si un jour on doit décoder des composants entrants complets.

## Routeur d'events Gateway (v1 livrée)

`discordClient.on<MessageCreateEvent> { reply { } }` : routeur réifié event→handler(s), multi-listeners, boucle de dispatch interne symétrique de celle des interactions, scope `EventScope<T>` avec `reply { }` réutilisant `ResponseScope` (boutons `.click` gratuits sur un message d'event). Voir `core/src/events/EventScope.kt`, `core/src/DiscordClient.kt`. Dette résiduelle :

- [ ] **Handlers d'events en mémoire.** `eventHandlers` (classe d'event → handlers) vit dans `DiscordClient` : perdu au redémarrage. Pas de désenregistrement (`off`).
- [ ] **Match par classe exacte.** Le routage matche `event::class` exactement : pas de handler « catch-all » (sur `DispatchEvent`) ni de dispatch par hiérarchie. À étendre si besoin.
- [ ] **`reply` limité aux events porteurs d'un channel.** Fourni pour `MessageCreateEvent`/`MessageUpdateEvent` (extensions typées, `@JvmName` pour l'erasure). Les autres events accèdent au client via `event` + les fonctions REST existantes.
- [ ] **Multi-handlers non ordonnancés.** Chaque handler d'un même type tourne dans son propre coroutine : pas de garantie d'ordre ni de « stop propagation ».

## Divers (déjà noté dans CLAUDE.md, rappelé ici)

- [x] `MessageUpdateEvent` (`MessageEvents.kt`) n'a pas l'annotation `@Serializable`.
