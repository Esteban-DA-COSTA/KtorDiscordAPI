# Utilisateurs

Lire des utilisateurs et ouvrir un canal de message privé.

## L'utilisateur courant (le bot)

```kotlin
```
{ src="restCurrentUser.kt"}

## Un utilisateur par id

```kotlin
```
{ src="restGetUser.kt"}

## Envoyer un message privé

Envoyer un DM se fait en deux temps : ouvrir (ou récupérer) le canal DM avec `createDM`, puis y envoyer
un message comme dans n'importe quel salon.

```kotlin
```
{ src="restCreateDM.kt"}

## Récapitulatif

| Fonction | Rôle |
|---|---|
| `getCurrentUser()` | Lire le compte du bot |
| `getUser(userId)` | Lire un utilisateur |
| `createDM(recipientId)` | Ouvrir un canal de message privé |

> Un utilisateur peut avoir désactivé les DM venant des membres du serveur : l'envoi échoue alors avec
> un `DiscordResponse.Failure`. Traite ce cas plutôt que de supposer le succès.
>
{style="note"}
