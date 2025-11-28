# Basic usage

## Initialisation

Il suffit de créer une instance de la classe `DiscordClient` en passant le token Discord en paramètre.

```kotlin
```
{ src="basicUsage.kt"}

## Envoyer un message
Pour envoyer un message, il suffit d'appeler la méthode `sendMessage` de l'instance de `DiscordClient`.

Cette méthode prend deux paramètres :

channelId
: L'identifiant du canal Discord où envoyer le message.
{type="medium"}

messageBuilder
: DSL Builder pour construire le message.
{type="medium"}

```kotlin
```
{src="basicSendMessage.kt"}