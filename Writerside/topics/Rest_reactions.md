# Réactions &amp; émojis

Ajouter/retirer des réactions sur un message, et gérer les émojis personnalisés d'un serveur.

## Réagir à un message

`createReaction` ajoute la réaction du bot. L'émoji est passé sous forme de chaîne :

- un **émoji unicode** directement (`"👍"`) ;
- un **émoji custom** au format `name:id` (par ex. `"partyblob:12345"`).

```kotlin
```
{ src="restReactions.kt"}

## Retirer des réactions

| Fonction | Effet |
|---|---|
| `deleteOwnReaction(channelId, messageId, emoji)` | Retire la réaction du bot |
| `deleteUserReaction(channelId, messageId, emoji, userId)` | Retire la réaction d'un utilisateur |
| `deleteAllReactionsForEmoji(channelId, messageId, emoji)` | Retire toutes les réactions d'un émoji |
| `deleteAllReactions(channelId, messageId)` | Retire **toutes** les réactions |
| `getReactions(channelId, messageId, emoji, …)` | Liste les utilisateurs ayant réagi |

## Émojis personnalisés d'un serveur

```kotlin
```
{ src="restEmojis.kt"}

| Fonction | Rôle |
|---|---|
| `listGuildEmojis(guildId)` | Lister les émojis du serveur |
| `getGuildEmoji(guildId, emojiId)` | Lire un émoji |
| `createGuildEmoji(guildId, payload)` | Ajouter un émoji |
| `modifyGuildEmoji(guildId, emojiId, payload)` | Renommer / re-catégoriser |
| `deleteGuildEmoji(guildId, emojiId)` | Supprimer un émoji |

> Pour un émoji custom dans une **réaction**, le format attendu est `name:id` — sans les deux-points
> englobants `<:...:>` de la syntaxe d'affichage des messages.
>
{style="note"}
