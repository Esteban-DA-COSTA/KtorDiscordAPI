# Architecture

Cette section s'adresse aux contributeurs et aux curieux : elle décrit comment la bibliothèque est
découpée. Un simple consommateur n'a pas besoin de la lire — il ne dépend que du module `core`.

## Les modules

```
app ──► core ──► components   (exported)
              └► websocket ──► components   (exported)
```

| Module | Rôle |
|---|---|
| `components` | Modèles de données Discord purs (`@Serializable`), enums, objets d'interaction, DSL builders. Aucune dépendance hors kotlinx.serialization. |
| `websocket` | Couche Gateway : session WebSocket, heartbeat, reconnexion/resume, opcodes, hiérarchie d'événements et leur désérialisation. |
| `core` | Façade publique : `DiscordClient`, appels REST, endpoints. Ré-exporte `components` et `websocket`. |
| `app` | Bot de démonstration (`app/src/Main.kt`). |

Les dépendances de `core` vers `components` et `websocket` sont **`exported`** : un utilisateur qui
importe `core` a tout le nécessaire, sans déclarer d'autres dépendances.

## Build : Kotlin Toolchain

Le projet n'utilise **pas Gradle** mais la Kotlin Toolchain de JetBrains. La configuration se fait via
`project.yaml` (racine) et un `module.yaml` par module.

```bash
./kotlin build   # compile tout
./kotlin run     # lance le module app
./kotlin test    # tests de désérialisation
```

Particularité de layout : les sources sont **directement sous `<module>/src/`** (pas de
`src/main/kotlin`), les tests sous `<module>/test/`.

## Où trouve-t-on quoi

| Besoin | Emplacement |
|---|---|
| Point d'entrée, config du client | `core/src/DiscordClient.kt` |
| Appels REST | `core/src/rest/*Requests.kt` (un fichier par ressource) |
| Segments d'URL | `core/src/DiscordEndpoints.kt` |
| Routage des interactions | `core/src/interactions/`, `dispatchInteraction` |
| Cycle de vie Gateway | `websocket/src/gateway/DiscordWebSocketSession.kt` |
| Désérialisation des événements | `websocket/src/gateway/events/Event.kt` |
| Modèles Discord | `components/src/components/*.kt` |
| DSL builders | `components/src/builders/`, `core/src/interactions/ResponseBuilder.kt` |
