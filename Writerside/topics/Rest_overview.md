# Vue d'ensemble &amp; réponses

Tous les appels à l'API REST de Discord sont exposés comme des méthodes **suspendues** sur le
`DiscordClient` : envoyer un message, récupérer un salon, bannir un membre… On les appelle depuis une
coroutine, à tout moment après la création du client.

```kotlin
```
{ src="basicSendMessage.kt"}

Les topics suivants regroupent ces appels par ressource :

<table>
    <tr><td>Ressource</td><td>Exemples</td></tr>
    <tr><td><a href="Rest_messages.md">Messages</a></td><td>envoyer, éditer, supprimer, épingler</td></tr>
    <tr><td><a href="Rest_channels.md">Salons</a></td><td>lire, modifier, supprimer, « écrit… »</td></tr>
    <tr><td><a href="Rest_guilds.md">Serveurs</a></td><td>membres, rôles, bannissements</td></tr>
    <tr><td><a href="Rest_reactions.md">Réactions &amp; émojis</a></td><td>réagir, lister, gérer les émojis</td></tr>
    <tr><td><a href="Rest_users.md">Utilisateurs</a></td><td>utilisateur courant, DM</td></tr>
</table>

## Traiter la réponse : `DiscordResponse<T>`

Chaque appel REST renvoie un `DiscordResponse<T>` qui porte **à la fois** le statut HTTP **et** le
résultat. C'est un type scellé à deux cas, qu'on discrimine avec un `when` exhaustif :

DiscordResponse.Success
: L'appel a réussi (2xx). Expose `value` — le corps désérialisé et typé (ici un `Message`) — et
`status`.
{type="medium"}

DiscordResponse.Failure
: L'appel a échoué (non-2xx). Expose `status` et `error`, le corps d'erreur Discord typé
(`DiscordError` : `code`, `message`), ou `null` si Discord n'a pas renvoyé de corps exploitable.
{type="medium"}

```kotlin
```
{ src="basicHttpCall.kt"}

## Les helpers

Pour les cas courants, des helpers évitent le `when` complet :

```kotlin
```
{ src="basicResponseHelpers.kt"}

getOrNull()
: Renvoie l'objet en cas de succès, ou `null` en cas d'échec.
{type="medium"}

getOrThrow()
: Renvoie l'objet en cas de succès, ou lève une `DiscordApiException` (portant le `status` et
l'`error`) en cas d'échec.
{type="medium"}

onSuccess { } / onFailure { }
: Exécutent un bloc uniquement dans le cas correspondant et renvoient le `DiscordResponse` pour le
chaînage.
{type="medium"}

> Les appels sans corps utile (par ex. les `DELETE`, qui renvoient un `204 No Content`) renvoient un
> `DiscordResponse<Unit>` : le succès ne porte pas d'objet, mais le statut et la gestion d'erreur
> restent identiques.
>
{style="tip"}

## Identifiants : `Snowflake`

Tous les identifiants Discord (salons, messages, serveurs, utilisateurs…) sont typés `Snowflake`, pas
`String`. Convertis une chaîne avec l'extension `.snowflake` :

```kotlin
```
{ src="snowflakeUsage.kt"}

Restent en `String`/`Int` les identifiants qui ne sont **pas** des snowflakes : tokens, `custom_id`,
bitfields de permissions, `session_id`.

## Rate limit

> Les erreurs `429` (rate limit) sont gérées **automatiquement** par le client, qui réessaie en
> respectant le délai indiqué par Discord. Tu n'as pas à les traiter toi-même.
>
{style="note"}
