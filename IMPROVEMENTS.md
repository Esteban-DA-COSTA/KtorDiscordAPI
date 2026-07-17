# Améliorations identifiées

Dette technique et pistes d'amélioration relevées lors de l'audit du 2026-07-17. Cocher au fur et à mesure ; supprimer une entrée si elle devient sans objet.

## Priorité 1 — Robustesse face à l'API Discord

- [ ] **1. Tolérance aux événements/opcodes inconnus.** `DispatchEvents.valueOf(eventName)` (`websocket/src/gateway/events/Event.kt`) lève une exception sur tout événement dispatch non listé (`GUILD_MEMBER_UPDATE`, `TYPING_START`…), `OPCodeSerializer` fait de même pour un opcode inconnu, et `InteractionData.Serializer` a un `TODO(type!!.name)` pour tous les types sauf `APPLICATION_COMMAND`. Chaque occurrence plante la boucle de session et force une reconnexion. → Introduire un fallback `UnknownEvent(name, data)` loggé en debug au lieu de lever. Meilleur ratio effort/bénéfice du projet.

- [ ] **2. Sérialiseurs d'enums entiers : lookup par id + factorisation.** `InteractionTypes.Serializer` fait `entries[decodeInt() - 1]` (fragile, dépend de la contiguïté des ids). → Rechercher par id comme `OPCodeSerializer`, et factoriser le pattern copié-collé dans une classe de base réutilisable type `IntEnumSerializer` avec valeur `UNKNOWN` de repli (Discord ajoute régulièrement des valeurs).

- [ ] **3. Bugs de décodage `MESSAGE_CREATE`/`MESSAGE_UPDATE`** (`EventSerializer.decodeDispatchEvent`) :
  - `data.jsonObject["guild_id"]?.jsonPrimitive?.toString()` retourne la représentation JSON **avec guillemets** (`"\"123\""`) au lieu de la valeur → utiliser `.content`. Toute comparaison sur `guildId` est silencieusement fausse aujourd'hui.
  - `member!!` et `mentions!!` : NPE pour les messages hors guilde (DM) → rendre le décodage nullable.

## Priorité 2 — Design de la bibliothèque

- [ ] **4. Supprimer le `runBlocking` de l'`init` de `DiscordClient`.** Constructeur bloquant, non testable, qui exige réseau + token valide. → Factory suspend (`DiscordClient.create(token)`) ou récupération paresseuse de `applicationId` au `login()`.

- [ ] **5. Channels rendezvous → bufferisés (ou `SharedFlow`).** `Channel<DispatchEvent>()` sans buffer : si le consommateur est lent ou absent (bot qui ne lit que `interactions`), la boucle Gateway se suspend sur `send` et ne traite plus aucune frame. → `Channel(BUFFERED)` au minimum ; `SharedFlow` pour permettre plusieurs consommateurs.

- [ ] **6. Concurrence structurée dans `DiscordWebSocketSession`.** Les `CoroutineScope(Dispatchers.Default).launch` ad hoc (connexion, heartbeat) ne sont rattachés à rien : impossible d'arrêter proprement le client. → Scope porté par `DiscordClient` + méthode `close()`.

- [ ] **7. Séparer payloads sortants et modèles entrants.** `Message` sert à la fois de DTO d'envoi (DSL `sendMessage { }`) et de modèle reçu (`MESSAGE_CREATE`), d'où les `var` nullables partout. Le bon pattern existe déjà avec `RolePayload` → le généraliser (payloads mutables pour l'envoi, modèles immuables `val` en réception). Inclut le `Snowflake("-1")` factice de `createGlobalApplicationCommand` → créer un `ApplicationCommandPayload` sans `id`.

- [ ] **8. Gestion du rate limiting REST (429).** Les appels retournent le `HttpResponse` brut sans détection ni retry. → Intercepteur Ktor minimal : lire `Retry-After`, attendre, rejouer.

## Priorité 3 — Hygiène / cosmétique

- [ ] **9. Adopter une racine de package commune.** Les fichiers de `core` sont dans le package par défaut (inimportable depuis Java, risque de collisions). → `ktordiscord.core`, `ktordiscord.gateway`, etc. Refactor mécanique, à faire tôt tant qu'il n'y a pas de consommateurs externes.

- [ ] **10. Dégraisser le DSL `DiscordMessageBuilder.kt`.** Les fonctions type `Embed.title(title)` doublonnent les propriétés `var` assignables dans le lambda. Bug au passage : `EmbedAuthor.iconUrl(url)` assigne `this.url` au lieu de `this.iconUrl`. → Garder les propriétés ; ne conserver que les fonctions à valeur ajoutée (`embed { }`, `field { }`, `color(Color)`).

- [ ] **11. Ajouter des tests unitaires de désérialisation.** `EventSerializer`, les enums et `Snowflake` sont des fonctions pures, testables sans réseau avec des payloads JSON réels de Discord. Verrouille les points 1 à 3. Rappel : le projet n'a actuellement **aucun test** (`./amper test`).

## Divers (déjà noté dans CLAUDE.md, rappelé ici)

- [ ] `MessageUpdateEvent` (`MessageEvents.kt`) n'a pas l'annotation `@Serializable`.
