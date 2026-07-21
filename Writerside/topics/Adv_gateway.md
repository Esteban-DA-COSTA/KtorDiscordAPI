# Cycle de vie de la Gateway

[`login()`](Connexion.md) gère seul la robustesse de la connexion WebSocket. Cette page décrit ce qui
se passe sous le capot — utile pour diagnostiquer une déconnexion ou contribuer. Rien ici n'est à
câbler côté consommateur.

Tout est piloté par `DiscordWebSocketSession` (`websocket/src/gateway/DiscordWebSocketSession.kt`).

## Handshake

1. Le client se connecte à l'URL de la Gateway et reçoit un `HelloEvent` portant l'intervalle de
   heartbeat.
2. Il envoie un `IdentifyEvent` (token + intents).
3. Discord répond par un `ReadyEvent`, qui porte le **`sessionId`** et la **`resumeGatewayUrl`** —
   mémorisés pour un éventuel resume.

## Heartbeat

Le client envoie un heartbeat à l'intervalle donné par `HelloEvent`, avec un **jitter initial**
(premier délai aléatoire) conforme aux recommandations de Discord. Chaque heartbeat doit être acquitté
(`HeartbeatACK`).

> **Détection de connexion zombie** : si un ACK manque avant le heartbeat suivant, la connexion est
> considérée morte ; le client la ferme et relance la boucle de connexion.
>
{style="note"}

## Reconnexion &amp; resume

En cas de coupure, le client relance sa boucle avec un **backoff aléatoire**. Deux issues :

- **Resume** : il rouvre la session sur `resumeGatewayUrl` et envoie un `ResumeEvent`
  (`sessionId` + dernier `sequenceId` reçu) pour rejouer les événements manqués.
- **Re-identify** : si Discord invalide la session (`InvalidSessionEvent`, close code `4009`), il
  repart sur un `IdentifyEvent` neuf.

## Close codes fatals

Certains close codes sont **non récupérables** : le client s'arrête plutôt que de boucler en vain.

| Code | Sens |
|---|---|
| `4004` | Token invalide |
| `4010` | Sharding invalide |
| `4011` | Sharding requis |
| `4012` | Version de Gateway invalide |
| `4013` | Intents invalides |
| `4014` | Intents non autorisés (privilégié non activé) |

> Un close code `4014` signifie presque toujours un **intent privilégié non coché** dans le portail
> développeur. Voir [Se connecter](Connexion.md#les-intents).
>
{style="warning"}

## Routage interne des événements

La session route les `InteractionCreateEvent` vers le canal `interactions` (d'où partent le routage
des [commandes](Commands.md) et des [composants](Components.md)) et **tout le reste** vers le canal
`events` (consommé par les handlers [`on<T>`](Events.md)).
