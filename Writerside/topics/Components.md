# Composants &amp; boutons

Les **composants** sont les éléments interactifs attachés à un message (pour l'instant : les
**boutons**). La bibliothèque offre deux façons de router un clic, selon que le bouton est **jetable**
ou **persistant**.

## Ajouter un bouton (clic inline)

Dans n'importe quel [builder de message](Message_dsl.md) — `respond { }`, `sendMessage { }`,
`reply { }` — `button(...)` ajoute un bouton et renvoie une poignée sur laquelle `.click { }` attache
le callback exécuté au clic. Un `custom_id` est généré automatiquement.

```kotlin
```
{ src="interactionButton.kt"}

Depuis un callback de clic, on peut `respond` (nouveau message) ou `update` (modifier le message
porteur) — voir [Réponses aux interactions](Interaction_responses.md).

> Le style `ButtonStyle.LINK` ouvre une URL et n'émet **aucune** interaction : `.click { }` y est sans
> effet.
>
{style="note"}

## Boutons éphémères vs persistants

Le `.click { }` inline est **éphémère** : la closure est stockée dans un cache borné (LRU + TTL, voir
[Configurer le client](Client.md#le-cache-de-composants-ephemeres)). Il est **perdu au redémarrage** et
peut être évincé — à réserver au court terme.

Pour un bouton qui doit survivre à un redémarrage (menu de rôles, panneau de config…), utilise un
`custom_id` **stable** et enregistre son handler **au niveau du client**.

```kotlin
```
{ src="interactionComponentTopLevel.kt"}

| | Éphémère (`.click { }`) | Persistant (`on(Component, id)`) |
|---|---|---|
| `custom_id` | auto-généré (UUID) | choisi, stable |
| Survit au redémarrage | ❌ | ✅ (si le handler est ré-enregistré au démarrage) |
| Stockage | cache borné | registre non borné |
| Usage | boutons court-lived | vues persistantes |

## Routage par préfixe et argument

Un handler déclaré matche par **préfixe** = la partie du `custom_id` **avant le premier `:`**. Le `:`
est un **séparateur réservé** : ce qui suit est un état lu via `arg`.

```kotlin
```
{ src="componentArg.kt"}

Ainsi un même handler `"approve"` traite `approve:42`, `approve:99`… et lit l'id via `arg`. Un
`custom_id` sans `:` matche à l'identique.

> Ordre de résolution d'un clic : handler **déclaré** (par préfixe) d'abord, puis cache **éphémère**.
>
{style="tip"}
