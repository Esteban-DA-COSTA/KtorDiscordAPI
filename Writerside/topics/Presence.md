# Présence &amp; activité

`updatePresence { }` change le **statut** du bot (en ligne, occupé…) et son **activité** (« joue à… »,
« écoute… »). C'est l'opération Gateway OP 3.

```kotlin
```
{ src="presenceUpdate.kt"}

> `updatePresence` nécessite une connexion Gateway active ([`login`](Connexion.md)). Sans connexion,
> l'appel est ignoré avec un avertissement dans les logs.
>
{style="note"}

## Le statut

La propriété `status` prend une valeur de `StatusTypeEnum` :

| Valeur | Rendu Discord |
|---|---|
| `ONLINE` | En ligne (défaut) |
| `DO_NOT_DISTURB` | Ne pas déranger |
| `AFK` | Inactif (« idle ») |
| `INVISIBLE` | Hors ligne (apparent) |

`afk` (booléen) et `since` (timestamp d'inactivité) sont également réglables.

## L'activité

Un bot n'affiche **qu'une seule activité** à la fois. Chaque helper remplace la précédente :

| Helper | Rendu |
|---|---|
| `playing("...")` | Joue à … |
| `streaming("...", url)` | Streame … (lien Twitch/YouTube) |
| `listening("...")` | Écoute … |
| `watching("...")` | Regarde … |
| `competing("...")` | Participe à … |
| `custom("...")` | Texte brut, sans préfixe |

```kotlin
```
{ src="presenceActivity.kt"}

> Ne définis **pas** deux activités en appelant deux helpers : seul le dernier appel est conservé,
> conformément à la limite d'une activité par bot côté Discord.
>
{style="tip"}
