# Commandes d'application

Les **commandes d'application** (slash commands, ex. `/ping`) se déclarent et se gèrent au même endroit,
via `on(nomDeLaCommande) { }`. Le bloc reçu (`CommandScope`) permet à la fois de **déclarer** la
commande auprès de Discord et d'enregistrer **comment** y répondre.

## Déclarer &amp; gérer en un bloc

```kotlin
```
{ src="commandDeclare.kt"}

- `define { }` **déclare** la commande. Les définitions sont collectées et poussées à
  [`login()`](Connexion.md).
- `respond { }` enregistre la **réponse** (raccourci ; voir aussi `defer`/`handle` ci-dessous).

> Enregistre tes commandes **avant** `login()` : c'est `login()` qui déclenche la synchronisation vers
> Discord.
>
{style="tip"}

## Synchronisation déclarative (bulk overwrite)

À `login()`, la bibliothèque pousse les commandes via un **bulk overwrite**, une requête par scope
(global / par serveur). Conséquence importante :

> Une commande **non** déclarée avec `define` dans un scope est **supprimée** de Discord pour ce scope.
> Le code Kotlin est la source de vérité : ce que tu déclares = l'ensemble exact des commandes du bot.
>
{style="warning"}

## Global vs serveur

Par défaut, une commande est **globale**. Passe un `guildId` à `define` pour une commande **de
serveur** :

```kotlin
```
{ src="commandGuild.kt"}

| Portée | Déclaration | Propagation |
|---|---|---|
| Globale | `define { }` | Jusqu'à ~1 h |
| Serveur | `define(guildId) { }` | **Instantanée** (idéal en développement) |

## Les verbes de gestion

Dans le `CommandScope`, plusieurs formes coexistent selon le besoin :

respond { }
: Raccourci pour la réponse statique courante. Équivaut à `handle { respond(init) }`.
{type="medium"}

defer()
: Raccourci qui accuse réception en mode « réfléchit… » ; la réponse est complétée plus tard (voir
[Réponses aux interactions](Interaction_responses.md#reponses-differees)).
{type="medium"}

handle { }
: Forme générale : le handler s'exécute avec un `CommandInteractionScope` complet (accès à
`interaction`, `respond`, `defer`, `editOriginal`, branchements…). À utiliser dès que la réponse
dépend du contexte.
{type="medium"}

```kotlin
```
{ src="commandHandle.kt"}

## Déclaration bas niveau (REST)

`define { }` couvre le cas normal. Si tu as besoin de piloter les commandes manuellement (audit,
migration…), les appels REST bruts existent : `createGlobalApplicationCommand`,
`bulkOverwriteGuildApplicationCommands`, `deleteGlobalApplicationCommand`, etc. Ils renvoient un
[`DiscordResponse`](Rest_overview.md) comme tout appel REST.
