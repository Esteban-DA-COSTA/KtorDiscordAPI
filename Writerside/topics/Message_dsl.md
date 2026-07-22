# Le DSL de message

Partout où KDA produit un message — que tu l'envoies toi-même, que tu répondes à un événement ou à une
interaction — tu passes le **même DSL de construction**. Apprends-le une fois, réutilise-le partout.

Ce topic couvre le **corps du message** (texte, options, réponses éphémères). Les deux briques riches
ont leur propre référence : [Embeds](Message_dsl_embeds.md) et [Boutons](Message_dsl_buttons.md).

## Où s'ouvre le DSL

Toutes ces fonctions prennent un bloc `ResponseScope.() -> Unit` — c'est le DSL décrit ici :

| Fonction | Contexte | Effet |
|---|---|---|
| `sendMessage(channelId) { }` | [client](Rest_messages.md) | envoie un message dans un salon |
| `reply { }` | [événement](Events.md) message | répond dans le salon du message reçu |
| `respond { }` | [interaction](Interaction_responses.md) | répond à une commande ou à un composant |
| `update { }` | interaction de composant | édite le message porteur du composant |
| `editOriginal { }` | interaction | édite la réponse déjà envoyée (après `defer`) |

`defer()` (sans bloc) accuse réception sans corps ; tu complètes ensuite avec `editOriginal { }`. Voir
[Réponses aux interactions](Interaction_responses.md).

## Le corps du message

Dans le bloc, tu règles directement les champs du message :

| Champ | Type | Défaut | Rôle |
|---|---|---|---|
| `content` | `String?` | `null` | le texte du message |
| `tts` | `Boolean` | `false` | lecture *text-to-speech* |
| `flags` | `Int?` | `null` | bitfield brut de flags (voir [`ephemeral()`](#reponses-ephemeres)) |

```kotlin
```
{ src="dslContent.kt"}

À cela s'ajoutent deux briques qui s'attachent via des sous-blocs plutôt que par affectation :

- `embed { }` — ajoute un embed (autant que tu veux). → [Référence des embeds](Message_dsl_embeds.md)
- `button(...) { }` — ajoute un bouton, rangé automatiquement en *action row*. →
  [Référence des boutons](Message_dsl_buttons.md)

> Il n'y a pas de setter `embeds =` ni `components =` : ces collections se remplissent uniquement via
> `embed { }` et `button(...)`.
>
{style="note"}

## Réponses éphémères

Dans une réponse d'interaction, `ephemeral()` marque le message comme visible du **seul utilisateur**
qui a déclenché l'interaction :

```kotlin
```
{ src="interactionEphemeral.kt"}

En interne, `ephemeral()` positionne le flag `1 shl 6` dans `flags`. C'est le seul flag exposé par un
helper ; tout autre flag se règle à la main via `flags`.

> `ephemeral()` n'a de sens que sur une **réponse d'interaction** (`respond` / `update`). Sur un
> `sendMessage` classique, Discord l'ignore.
>
{style="note"}

## Récupérer le message créé

`sendMessage` et `reply` renvoient un [`DiscordResponse`](Rest_overview.md) que tu peux dérouler :

```kotlin
```
{ src="basicResponseHelpers.kt"}

## Aller plus loin

<table>
    <tr><td>Objectif</td><td>Topic</td></tr>
    <tr><td>Titre, description, champs, images, couleur…</td><td><a href="Message_dsl_embeds.md">Embeds</a></td></tr>
    <tr><td>Boutons, styles, emoji, action rows</td><td><a href="Message_dsl_buttons.md">Boutons</a></td></tr>
    <tr><td>Câbler le clic d'un bouton</td><td><a href="Components.md">Composants &amp; boutons</a></td></tr>
    <tr><td>Répondre à une commande</td><td><a href="Interaction_responses.md">Réponses aux interactions</a></td></tr>
</table>
