# Construire des messages

Partout où l'on produit un message — [`sendMessage`](Rest_messages.md), [`reply`](Events.md),
[`respond`](Interaction_responses.md), [`update`](Interaction_responses.md) — on passe le même **DSL de
construction**. Ce topic en fait le tour.

## Le corps du message

Le champ de base est `content` (le texte). On peut aussi marquer le message `tts`, régler les `flags`,
ou attacher des embeds et des boutons.

```kotlin
```
{ src="dslContent.kt"}

## Embeds

`embed { }` ajoute un embed. Ses champs directs : `title`, `description`, `url`, plus les sous-blocs
`author { }`, `footer { }`, `image { }`, `thumbnail { }`, et un nombre libre de `field { }`.

```kotlin
```
{ src="dslEmbed.kt"}

### Couleur

`color(...)` prend une `java.awt.Color`, stockée en RGB. Utilise une constante ou une valeur hex :

```kotlin
```
{ src="dslColor.kt"}

### Champs (`field`)

Chaque `field { }` a un `name`, une `value` et un booléen `inline` (les champs `inline` s'alignent
côte à côte).

```kotlin
```
{ src="dslField.kt"}

## Boutons

`button(...)` ajoute un bouton. Les boutons sont automatiquement rangés dans des *action rows*
(5 boutons max par ligne). Le style se règle dans le bloc de configuration.

```kotlin
```
{ src="dslButton.kt"}

| Style | Rendu |
|---|---|
| `PRIMARY` | Bleu (action principale) |
| `SECONDARY` | Gris |
| `SUCCESS` | Vert |
| `DANGER` | Rouge |
| `LINK` | Bouton-lien (ouvre une `url`, pas de clic routé) |

Le câblage du clic (`.click { }`, `custom_id` persistant) est décrit dans
[Composants &amp; boutons](Components.md).

> Le `custom_id` d'un bouton est limité à **100 caractères** par Discord ; `button()` le vérifie et
> lève une erreur au-delà.
>
{style="note"}
