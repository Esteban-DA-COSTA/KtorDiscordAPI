# CLAUDE.md

Ce fichier guide Claude Code lorsqu'il travaille sur ce dépôt.

## Vue d'ensemble

**KtorDiscordAPI** est une bibliothèque Kotlin/JVM qui encapsule l'API Discord (REST + Gateway WebSocket) pour écrire des bots. Elle est construite sur :

- **Ktor Client 3.x** (moteur CIO) pour le HTTP et le WebSocket
- **kotlinx.serialization** pour le JSON (avec beaucoup de sérialiseurs custom, voir plus bas)
- **kotlinx.coroutines** pour l'asynchrone (les événements sont distribués via des `Channel`)
- **kotlin-logging** (io.github.oshai) + Logback pour les logs

L'API Discord ciblée est la **v10** (`DiscordClient.apiVersion`).

## Build : Amper (pas Gradle)

Le projet utilise [Amper](https://github.com/JetBrains/amper) de JetBrains. Il n'y a **aucun fichier Gradle** ; la configuration se fait via `project.yaml` (racine) et un `module.yaml` par module.

```bash
./amper build          # compile tout (amper.bat sous Windows)
./amper run            # lance le module app (jvm/app)
./amper test           # (aucun test n'existe actuellement)
```

Particularité du layout Amper : les sources sont **directement sous `<module>/src/`** — il n'y a pas de `src/main/kotlin`.

## Architecture des modules

Déclarés dans `project.yaml` : `app`, `core`, `components`, `websocket`.

```
app ──► core ──► components   (exported)
              └► websocket ──► components   (exported)
```

| Module | Rôle |
|---|---|
| `components` | Modèles de données Discord purs (`@Serializable`), enums, objets d'interaction, DSL builders. Aucune dépendance hors kotlinx.serialization. |
| `websocket` | Couche Gateway : session WebSocket, heartbeat, reconnexion/resume, OPCodes, hiérarchie d'événements et leur désérialisation. |
| `core` | Façade publique : `DiscordClient`, appels REST, endpoints. Ré-exporte `components` et `websocket` (dépendances `exported`) — un utilisateur ne dépend que de `core`. |
| `app` | Bot de démonstration / terrain de jeu (`app/src/Main.kt`). |

**Ne sont pas des modules :**
- `httpRequests/` (racine) — fichiers `.http` IntelliJ pour tester l'API Discord à la main (les tokens sont dans `httpRequests/env/`, dont un fichier `.private.env.json`).
- `Writerside/` — documentation utilisateur (JetBrains Writerside), rédigée **en français**, avec ses snippets dans `Writerside/codeSnippets/`.
- `.claude/worktrees/` — copie résiduelle du projet (worktree). **À exclure des recherches** (grep/glob) pour éviter les doublons.

## Cartographie des fichiers

### `core/src/` (package par défaut, sans déclaration de package)
- `DiscordClient.kt` — point d'entrée de la lib. Configure le `HttpClient` avec **deux configs Json distinctes** : REST (`ignoreUnknownKeys`, `coerceInputValues`) et WebSocket (`explicitNulls=false`, `isLenient`, `encodeDefaults`). Expose deux `Channel` publics : `events` (`DispatchEvent`) et `interactions` (`Interaction`). `login(intents)` délègue à `DiscordWebSocketSession.connect`.
- `httpRequests.kt` — **tous les appels REST**, écrits comme des fonctions d'extension `suspend fun DiscordClient.xxx(...): HttpResponse`.
- `DiscordEndpoints.kt` — enum des segments d'URL (`applications`, `guilds`, `channels`…).
- `InteractionManager.kt` — cache des commandes d'application (embryonnaire).
- `Utils.kt` — `HttpRequestBuilder.buildDiscordHeader(token)` : ajoute le header `Authorization: Bot <token>`.

### `websocket/src/` (packages `gateway` et `gateway.events`)
- `DiscordWebSocketSession.kt` — cycle de vie complet de la connexion Gateway : boucle de connexion avec backoff aléatoire, heartbeat avec jitter initial, détection de connexion zombie (pas de ACK), **resume** (`sessionId` + `resumeGatewayUrl` capturés sur `ReadyEvent`), close codes fatals (`4004, 4010-4014`) et re-identify (`4009`). Route les `InteractionCreateEvent` vers le channel `interactions`, tout le reste vers `events`.
- `gateway/OPCode.kt` — enum des opcodes Gateway, sérialisée par entier via `OPCodeSerializer`.
- `gateway/DispatchEvents.kt` — enum des noms d'événements dispatch supportés (`READY`, `MESSAGE_CREATE`…). C'est la liste de référence : un nom reçu absent de cet enum fait échouer `valueOf`.
- `gateway/events/Event.kt` — **cœur de la désérialisation Gateway**. `sealed class Event` avec un `EventSerializer` écrit à la main qui lit l'enveloppe `{op, t, s, d}` puis dispatch : `op == DISPATCH` → `decodeDispatchEvent` (switch sur `DispatchEvents`), sinon `decodeClassicEvent` (switch sur `OPCode`).
- `gateway/events/*.kt` — un fichier par événement ou groupe d'événements (`MessageEvents.kt`, `ChannelEvents.kt`…). Les événements dispatch héritent de `DispatchEvent` (qui porte `sequenceId`, `@Transient`) ; les événements protocole (Hello, Heartbeat, Identify, Resume…) héritent directement de `Event`. Les événements **sortants** (`IdentifyEvent`, `ResumeEvent`, `HeartbeatEvent`) ont un sérialiseur custom qui écrit l'enveloppe `{op, d}`.

### `components/src/` (packages `components`, `components.enums`, `components.interactions`, `builders`)
- `components/*.kt` — un modèle Discord par fichier (`Message.kt` contient aussi `Embed` et ses sous-objets), tous en `data class @Serializable` avec propriétés `var` nullables pour les champs optionnels.
- `components/Snowflake.kt` — `value class Snowflake` (inline) avec sérialiseur custom, plus l'extension `String.snowflake`.
- `components/enums/*.kt` — enums typés Discord, souvent sérialisés par entier.
- `components/interactions/*.kt` — `Interaction`, `InteractionData` (hiérarchie scellée à désérialisation manuelle selon `type`), `ApplicationCommand`, `InteractionCallBack`.
- `builders/DiscordMessageBuilder.kt` — le DSL de construction de messages.

## Conventions et patterns de code

### Sérialisation (le pattern central du projet)
- Mapping snake_case Discord → camelCase Kotlin via `@SerialName("channel_id")` champ par champ (pas de naming strategy globale).
- **Sérialiseur custom en `companion object Serializer`** : c'est le pattern maison, utilisé par `Snowflake`, `InteractionTypes`, `InteractionData`. Les enums codés par entier implémentent `KSerializer` avec `PrimitiveKind.INT` (voir `OPCode` et `InteractionTypes`).
- **Polymorphisme manuel** : pas de `SerializersModule`/`classDiscriminator`. La hiérarchie `Event` est décodée à la main dans `EventSerializer` (lecture de l'enveloppe en `JsonElement` puis `decoder.json.decodeFromJsonElement(...)` sur le bon sérialiseur). Même approche pour `InteractionData`.
- Champs non sérialisés : `@Transient` (ex. `DispatchEvent.sequenceId`, `Interaction.data`).

### DSL builders
- `@DslMarker annotation class DiscordMessageBuilder` + fonctions d'extension annotées.
- Pattern récurrent dans toute l'API publique : paramètre `init: X.() -> Unit` consommé par `X().apply(init)` (ex. `sendMessage(channelId) { content = "..."; embed { title = "..." } }`).

### Appels REST
- Toujours des `suspend fun DiscordClient.xxx(...)` dans `core/src/httpRequests.kt`, retournant le `HttpResponse` brut (pas de désérialisation typée de la réponse pour l'instant).
- Toujours `buildDiscordHeader(token)` pour l'auth, `contentType(ContentType.Application.Json)` + `setBody(...)` pour les POST.
- URLs construites avec `$discordURL/${DiscordEndpoints.XXX.text}/...`.

### Distribution des événements
- Le consommateur itère sur les channels : `for (event in client.events) { when (event) { is MessageCreateEvent -> ... } }` (voir `app/src/Main.kt` pour l'exemple canonique).
- Les interactions ont leur propre channel `client.interactions`.

### Divers
- Loggers nommés : `KotlinLogging.logger("HTTP_LOGGER")` et `"WSS_LOGGER"`.
- KDoc en anglais sur l'API publique ; doc Writerside en français.
- Régions IDE `//#region ... //#endregion` pour structurer les gros fichiers.

## Recettes pour les modifications courantes

**Ajouter un endpoint REST** : ajouter une fonction d'extension dans `core/src/httpRequests.kt` (suivre le style existant : `buildDiscordHeader`, `DiscordEndpoints`), et compléter `DiscordEndpoints` si un nouveau segment d'URL est nécessaire. Exposer éventuellement une façade DSL dans `DiscordClient`.

**Ajouter un événement Gateway** :
1. Ajouter le nom dans l'enum `gateway/DispatchEvents.kt`.
2. Créer la classe dans `websocket/src/gateway/events/` (hériter de `DispatchEvent`, `override var sequenceId`, `@Serializable`).
3. Câbler le décodage dans le `when` de `EventSerializer.decodeDispatchEvent` (`Event.kt`).

**Ajouter un modèle Discord** : nouveau fichier dans `components/src/components/`, `data class @Serializable`, `@SerialName` pour chaque champ snake_case, `Snowflake` pour les IDs, champs optionnels nullables avec défaut `null`.

## Pièges connus / état du projet

> La dette technique détaillée et priorisée est tracée dans `IMPROVEMENTS.md` (cases à cocher). Consulter ce fichier avant d'entreprendre un refactor, et le mettre à jour quand un point est traité.

- **Aucun test** dans le projet, pas de CI. Vérifier au minimum que `./amper build` passe.
- `MessageUpdateEvent` (`MessageEvents.kt`) n'a pas l'annotation `@Serializable` (oubli probable).
- Le désérialiseur de `InteractionTypes` fait `entries[decodeInt() - 1]` : correct uniquement parce que les ids sont contigus à partir de 1 — ne pas répliquer ce pattern, préférer `entries.first { it.id == ... }` comme dans `OPCodeSerializer`.
- `DiscordClient` fait un `runBlocking` dans son `init` (récupération de l'applicationId) : la construction du client est bloquante et nécessite un token valide et du réseau.
- `app/src/Main.kt` contient un token placeholder (`"secret"`) et des IDs codés en dur — c'est un fichier de test manuel, ne jamais y committer un vrai token.
- Un événement dispatch reçu dont le nom n'est pas dans `DispatchEvents` lève une exception (`valueOf`), et un `OPCode` inconnu aussi — la gestion d'erreur de la boucle de session log puis reconnecte.
- Le décodage de `MESSAGE_CREATE`/`MESSAGE_UPDATE` suppose la présence de `member` et `mentions` (`!!`) : casse pour les messages hors guilde (DM).
