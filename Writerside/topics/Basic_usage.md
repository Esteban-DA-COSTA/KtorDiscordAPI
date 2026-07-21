# Basic usage

Ce topic présente les deux gestes de base pour utiliser la bibliothèque : **créer un client** et
**faire un appel à l'API Discord**.

## Créer un client

Tout part d'une instance de `DiscordClient`, construite avec la fonction `DiscordClient.create` en
lui passant le token du bot.

```kotlin
```
{ src="basicUsage.kt"}

token
: Le token d'authentification du bot Discord.
{type="medium"}

> `create` est une fonction **suspendue** : elle effectue un appel réseau à la construction (pour
> résoudre l'identifiant d'application du bot). Il faut donc l'appeler depuis une coroutine — d'où le
> `runBlocking { }` dans l'exemple — avec un token valide et un accès réseau.
>
{style="note"}

## Faire un appel HTTP

Les appels à l'API REST de Discord sont des fonctions **suspendues** exposées sur le client (envoyer
un message, récupérer un salon, bannir un membre…). Exemple avec `sendMessage`, qui envoie un
message dans un salon :

```kotlin
```
{src="basicSendMessage.kt"}

channelId
: L'identifiant du salon Discord où envoyer le message.
{type="medium"}

messageBuilder
: DSL de construction du message (`content`, `embed { }`, boutons…).
{type="medium"}

## Traiter la réponse

Chaque appel REST renvoie un `DiscordResponse<T>` qui porte **à la fois** le statut HTTP **et** le
résultat. C'est un type scellé à deux cas, qu'on peut discriminer avec un `when` exhaustif :

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

> Les erreurs `429` (rate limit) sont gérées automatiquement par le client, qui réessaie en
> respectant le délai indiqué par Discord. Tu n'as donc pas à les traiter toi-même.
>
{style="note"}
