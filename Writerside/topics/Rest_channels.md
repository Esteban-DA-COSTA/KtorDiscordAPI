# Salons

Lire, modifier et supprimer des salons, et déclencher l'indicateur « écrit… ». La création d'un salon
dans un serveur est côté [Serveurs](Rest_guilds.md#salons-d-un-serveur).

## Lire un salon

```kotlin
```
{ src="restGetChannel.kt"}

## Modifier / supprimer

`modifyChannel` prend un `ModifyChannelPayload` (nom, sujet, position, permissions…). `deleteChannel`
supprime le salon (ou ferme un DM).

```kotlin
```
{ src="restModifyChannel.kt"}

## Indicateur « écrit… »

`triggerTypingIndicator` affiche « le bot est en train d'écrire » pendant quelques secondes. Utile
avant une réponse longue à produire.

```kotlin
```
{ src="restTyping.kt"}

## Récapitulatif

| Fonction | Rôle |
|---|---|
| `getChannel(channelId)` | Lire un salon |
| `modifyChannel(channelId, payload)` | Modifier un salon |
| `deleteChannel(channelId)` | Supprimer un salon / fermer un DM |
| `triggerTypingIndicator(channelId)` | Afficher « écrit… » |

> Les opérations liées aux messages d'un salon (envoi, historique, épingles) sont regroupées dans
> [Messages](Rest_messages.md).
>
{style="note"}
