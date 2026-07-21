# Réponses aux interactions

Quand une interaction arrive (commande ou clic de bouton), le handler dispose de plusieurs **verbes**
pour y répondre. Les verbes disponibles dépendent de l'origine de l'interaction.

## Répondre

`respond { }` envoie un **nouveau** message de réponse. Il utilise le même
[builder](Message_dsl.md) que `sendMessage` (`content`, `embed { }`, boutons…).

```kotlin
```
{ src="interactionOn.kt"}

## Réponses différées
{ id="reponses-differees"}

Discord exige un accusé de réception sous **3 secondes**. Si le traitement est plus long, `defer()`
acquitte tout de suite (« réfléchit… »), puis `editOriginal { }` renseigne la réponse une fois le
travail terminé.

```kotlin
```
{ src="interactionDefer.kt"}

## Message éphémère

`ephemeral()` rend la réponse visible **uniquement** par l'utilisateur qui a déclenché l'interaction.

```kotlin
```
{ src="interactionEphemeral.kt"}

## Mettre à jour le message (composants)

Depuis un clic de bouton, `update { }` **modifie le message qui portait le bouton** au lieu d'en
envoyer un nouveau — idéal pour un menu qui évolue sur place.

```kotlin
```
{ src="interactionUpdate.kt"}

## Verbes disponibles selon l'origine

| Verbe | Commande | Clic sur bouton | Effet |
|---|:---:|:---:|---|
| `respond { }` | ✅ | ✅ | Envoie un nouveau message |
| `defer()` | ✅ | ✅ | Accuse réception, réponse renseignée plus tard |
| `editOriginal { }` | ✅ | ✅ | Modifie le message de réponse initial |
| `update { }` | ❌ | ✅ | Modifie le message auquel appartient le composant |

> `update { }` n'existe que pour les composants : une commande n'a pas de « message porteur » à
> modifier.
>
{style="note"}
