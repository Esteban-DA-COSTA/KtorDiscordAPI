# Embeds

Un **embed** est un encart riche (titre, description, champs, images, couleur) attaché à un message.
Tu en ajoutes un avec `embed { }` dans n'importe quel [bloc de construction de
message](Message_dsl.md) ; répète l'appel pour empiler plusieurs embeds.

```kotlin
```
{ src="dslEmbed.kt"}

## Les champs de l'embed

Dans le bloc `embed { }`, ces champs s'affectent directement :

| Champ | Type | Défaut | Rôle |
|---|---|---|---|
| `title` | `String?` | `null` | titre en tête de l'embed |
| `description` | `String?` | `null` | corps de texte principal |
| `url` | `String?` | `null` | rend le `title` cliquable |
| `timestamps` | `String?` | `null` | horodatage ISO 8601 affiché en pied |
| `color` | `Int?` | `null` | barre de couleur (RGB packé — voir [Couleur](#couleur)) |
| `type` | `EmbedTypes?` | `null` | type d'embed (`RICH` par défaut côté Discord) |

Les blocs suivants s'attachent via des sous-fonctions plutôt que par affectation :

| Sous-bloc | Objet | Rôle |
|---|---|---|
| `author { }` | `EmbedAuthor` | ligne d'auteur au-dessus du titre |
| `footer { }` | `EmbedFooter` | pied de l'embed |
| `image { }` | `EmbedImage` | grande image |
| `thumbnail { }` | `EmbedImage` | vignette (coin haut-droit) |
| `field { }` | `EmbedField` | un champ nom/valeur (répétable) |

> Le DSL expose aussi `video { }` et `provider { }` (mêmes objets `EmbedImage` / `EmbedProvider`),
> mais Discord renseigne ces champs lui-même pour les embeds automatiques (liens, vidéos) : les régler
> sur un embed sortant n'a en général aucun effet.
>
{style="note"}

## Couleur

```kotlin
```
{ src="dslColor.kt"}

`color(color: java.awt.Color)` stocke la couleur sous forme d'entier RGB packé. Tu peux aussi affecter
l'`Int` directement (`color = 0x5865F2`). Utilise une constante `java.awt.Color` (`Color.RED`…) ou une
valeur hexadécimale.

> Il n'existe **pas** d'enum de couleurs maison dans KDA : la couleur passe toujours par
> `java.awt.Color` ou par un `Int` RGB.
>
{style="note"}

## Auteur et pied de page

`EmbedAuthor` (`author { }`) :

| Champ | Type | Défaut |
|---|---|---|
| `name` | `String` | `""` |
| `url` | `String?` | `null` |
| `iconUrl` | `String?` | `null` |

`EmbedFooter` (`footer { }`) :

| Champ | Type | Défaut |
|---|---|---|
| `text` | `String` | `""` |
| `iconUrl` | `String?` | `null` |

## Images

`image { }`, `thumbnail { }` (et `video { }`) prennent tous un `EmbedImage`. À l'envoi, seul `url`
compte — les autres champs sont remplis par Discord dans la réponse :

| Champ | Type | Défaut | Note |
|---|---|---|---|
| `url` | `String` | `""` | l'URL de l'image (le seul à régler) |
| `proxyUrl` | `String?` | `null` | rempli par Discord (réponse) |
| `height` | `Int?` | `null` | rempli par Discord (réponse) |
| `width` | `Int?` | `null` | rempli par Discord (réponse) |

## Champs (`field`)

Chaque `field { }` ajoute un couple nom/valeur. Les champs `inline` s'alignent côte à côte (jusqu'à 3
par ligne côté Discord) ; les autres occupent toute la largeur.

```kotlin
```
{ src="dslField.kt"}

| Champ | Type | Défaut | Rôle |
|---|---|---|---|
| `name` | `String` | `""` | intitulé du champ |
| `value` | `String` | `""` | contenu |
| `inline` | `Boolean` | `false` | aligne le champ côte à côte avec ses voisins inline |

## Un embed complet

Tous les blocs réunis :

```kotlin
```
{ src="dslEmbedFull.kt"}

> KDA **ne valide pas** les limites Discord des embeds (256 car. pour le titre, 25 fields max, 6 000
> caractères cumulés…). Les dépasser fait échouer l'appel côté Discord, pas côté lib.
>
{style="warning"}

## La suite

<table>
    <tr><td>Objectif</td><td>Topic</td></tr>
    <tr><td>Texte, options, réponses éphémères</td><td><a href="Message_dsl.md">Le DSL de message</a></td></tr>
    <tr><td>Ajouter des boutons</td><td><a href="Message_dsl_buttons.md">Boutons</a></td></tr>
</table>
