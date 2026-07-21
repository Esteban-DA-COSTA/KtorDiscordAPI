# Messages

Envoyer, lire, modifier et supprimer des messages dans un salon. Tous ces appels renvoient un
[`DiscordResponse<T>`](Rest_overview.md).

## Envoyer un message

`sendMessage` prend un id de salon et un [bloc de construction](Message_dsl.md) (`content`, `embed`,
boutons…). C'est le point d'entrée le plus courant.

```kotlin
```
{ src="restSendMessage.kt"}

## Lire des messages

```kotlin
```
{ src="restReadMessages.kt"}

`getChannelMessages` accepte des paramètres de pagination (`limit`, `before`/`after`/`around`) pour
parcourir l'historique.

## Modifier &amp; supprimer

```kotlin
```
{ src="restEditDeleteMessage.kt"}

Pour faire le ménage en masse, `bulkDeleteMessages` supprime jusqu'à 100 messages en une requête
(Discord refuse les messages de plus de 2 semaines).

## Épingler

`pinMessage` / `unpinMessage` gèrent les messages épinglés ; `getPinnedMessages` les liste.

## Récapitulatif

| Fonction | Rôle |
|---|---|
| `sendMessage(channelId) { }` | Envoyer un message (avec DSL) |
| `createChannelMessage(channelId, payload)` | Envoyer un message (payload brut) |
| `getChannelMessage(channelId, messageId)` | Lire un message |
| `getChannelMessages(channelId, …)` | Lire l'historique (paginé) |
| `editChannelMessage(channelId, messageId, payload)` | Modifier un message |
| `deleteChannelMessage(channelId, messageId)` | Supprimer un message |
| `bulkDeleteMessages(channelId, messageIds)` | Supprimer en masse |
| `getPinnedMessages(channelId)` | Lister les épinglés |
| `pinMessage(channelId, messageId)` | Épingler |
| `unpinMessage(channelId, messageId)` | Désépingler |

> `sendMessage` peut attacher des boutons dont le clic est câblé inline via `.click { }` — voir
> [Composants &amp; boutons](Components.md).
>
{style="tip"}
