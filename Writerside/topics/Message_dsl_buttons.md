# Boutons

Un **bouton** est un composant cliquable attaché à un message. Tu l'ajoutes avec `button(...)` dans
n'importe quel [bloc de construction de message](Message_dsl.md). Ce topic couvre la **construction**
du bouton ; le **routage du clic** est détaillé dans [Composants &amp; boutons](Components.md).

```kotlin
```
{ src="dslButton.kt"}

## Ajouter un bouton

```
fun button(
    label: String? = null,
    customId: String? = null,
    init: (Button.() -> Unit)? = null,
): ButtonHandle
```

Tous les paramètres sont optionnels. La fonction renvoie un `ButtonHandle` sur lequel tu peux enchaîner
`.click { }` (voir [Réagir au clic](#reagir-au-clic)). Dans le bloc `init`, tu règles les propriétés du
bouton :

| Propriété | Type | Défaut | Rôle |
|---|---|---|---|
| `style` | `ButtonStyle` | `PRIMARY` | apparence / comportement |
| `label` | `String?` | `null` | texte affiché (souvent passé en 1<sup>er</sup> argument) |
| `customId` | `String?` | `null` | identifiant de routage du clic (≤ 100 car.) |
| `url` | `String?` | `null` | cible d'un bouton `LINK` |
| `emoji` | `Emoji?` | `null` | emoji affiché sur le bouton |
| `disabled` | `Boolean?` | `null` | grise le bouton (non cliquable) |

## Styles

| Style | Rendu |
|---|---|
| `PRIMARY` | Bleu (action principale) |
| `SECONDARY` | Gris |
| `SUCCESS` | Vert |
| `DANGER` | Rouge |
| `LINK` | Bouton-lien (ouvre une `url`, pas de clic routé) |

> Un bouton non-`LINK` a besoin d'un `custom_id` ; un bouton `LINK` a besoin d'une `url` à la place
> (et ne reçoit jamais de callback de clic).
>
{style="note"}

## Emoji et état désactivé

```kotlin
```
{ src="dslButtonEmoji.kt"}

L'`emoji` est un objet `Emoji` : `Emoji(name = "👍")` pour un emoji unicode, ou
`Emoji(id = ..., name = ..., animated = true)` pour un emoji custom de serveur.

## Action rows

Les boutons sont rangés automatiquement en *action rows* : chaque ligne accepte **5 boutons max**, et
KDA ouvre une nouvelle ligne au-delà. Tu n'as pas à instancier les `ActionRow` toi-même.

## Réagir au clic

Deux façons de router un clic — détaillées dans [Composants &amp; boutons](Components.md) :

**Éphémère** — `.click { }` génère un `custom_id` et stocke le callback dans un cache borné (LRU + TTL).
Simple, mais **perdu au redémarrage** et éventuellement évincé : à réserver au court terme.

```kotlin
```
{ src="interactionButton.kt"}

**Persistant** — donne un `custom_id` stable et enregistre le handler au niveau du client avec
`on(InteractionKind.Component, "prefix")`. Survit au redémarrage ; l'état encodé après le `:` se lit via
`arg`.

```kotlin
```
{ src="interactionComponentTopLevel.kt"}

> Le `custom_id` d'un bouton est limité à **100 caractères** ; `button()` vérifie la contrainte et lève
> une `IllegalArgumentException` au-delà.
>
{style="warning"}

> `.click { }` sur un bouton `LINK` n'a aucun effet (un bouton-lien n'émet pas d'interaction) : l'appel
> est ignoré avec un warning dans les logs.
>
{style="note"}

## La suite

<table>
    <tr><td>Objectif</td><td>Topic</td></tr>
    <tr><td>Router les clics, boutons persistants, <code>arg</code></td><td><a href="Components.md">Composants &amp; boutons</a></td></tr>
    <tr><td>Titre, champs, images, couleur…</td><td><a href="Message_dsl_embeds.md">Embeds</a></td></tr>
    <tr><td>Texte, options, réponses éphémères</td><td><a href="Message_dsl.md">Le DSL de message</a></td></tr>
</table>
