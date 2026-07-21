# Serveurs, membres, rôles &amp; bans

Les opérations liées à un serveur (« guild ») : lire le serveur, gérer ses membres, ses rôles, ses
salons et ses bannissements.

## Le serveur

```kotlin
```
{ src="restGetGuild.kt"}

## Salons d'un serveur

`getGuildChannels` liste les salons ; `createGuildChannel` en crée un.

```kotlin
```
{ src="restGuildChannels.kt"}

## Membres

```kotlin
```
{ src="restMembers.kt"}

`modifyGuildMember` change le pseudo, les rôles, l'état muet/sourd, le timeout… `removeGuildMember`
expulse (kick) un membre.

> Pour récupérer **tous** les membres d'un gros serveur, préfère la requête Gateway
> `requestGuildMembers` (OP 8) plutôt que de paginer `listGuildMembers`. Elle nécessite l'intent
> `GUILD_MEMBERS`.
>
{style="tip"}

## Rôles

```kotlin
```
{ src="restRoles.kt"}

`modifyRolePositions` réordonne plusieurs rôles en une requête (la hiérarchie des rôles).

## Bannissements

```kotlin
```
{ src="restBans.kt"}

`createGuildBan` accepte un `CreateBanPayload` optionnel (par ex. supprimer les messages récents du
banni). `removeGuildBan` lève le ban.

## Récapitulatif

| Domaine | Fonctions |
|---|---|
| Serveur | `getGuild` |
| Salons | `getGuildChannels`, `createGuildChannel` |
| Membres | `listGuildMembers`, `getGuildMember`, `addGuildMember`, `modifyGuildMember`, `removeGuildMember` |
| Rôles | `getRoles`, `createNewRole`, `editRole`, `modifyRolePositions`, `deleteRole` |
| Bans | `getGuildBans`, `getGuildBan`, `createGuildBan`, `removeGuildBan` |
