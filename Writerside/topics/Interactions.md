# Interactions

Les **interactions** regroupent tout ce à quoi un utilisateur peut réagir dans Discord :

- les **commandes d'application** (slash commands, ex. `/pingit`) ;
- les **composants de message** (pour l'instant les **boutons**).

La bibliothèque fournit un routeur : on associe un *handler* à une commande via `on(...)`, et l'on répond depuis ce handler. Un bouton ajouté dans une réponse attache directement son propre callback de clic — sans avoir à l'enregistrer séparément.

## Déclarer une commande

Une commande doit d'abord être déclarée auprès de Discord. C'est une opération à faire **une seule fois** (elle persiste côté Discord).

```kotlin
```
{ src="interactionRegisterCommand.kt"}

> Une commande **globale** peut mettre jusqu'à une heure à se propager sur tous les serveurs.
>
{style="note"}

## Réagir à une commande

On enregistre un handler avec `on(nomDeLaCommande)`. À l'intérieur, `respond { }` envoie le message de réponse (mêmes champs que `sendMessage { }` : `content`, `embed { }`, …).

```kotlin
```
{ src="interactionOn.kt"}

nomDeLaCommande
: Le nom de la commande, tel que déclaré via `createGlobalApplicationCommand`.
{type="medium"}

handler
: Bloc exécuté à chaque invocation de la commande. L'interaction reçue est accessible via `interaction`.
{type="medium"}

> Enregistre tes handlers **avant** d'appeler `login()`, afin de ne manquer aucune interaction.
>
{style="tip"}

`on("pingit")` est un raccourci de la forme complète `on(InteractionKind.Command, "pingit")`. La forme complète accepte une **clé typée** qui sélectionne le registre *et* le scope du handler :

- `InteractionKind.Command` : l'identifiant est un **nom de commande** ;
- `InteractionKind.Component` : l'identifiant est un **`custom_id`** de composant.

## Ajouter un bouton

Dans un bloc `respond { }`, `button(...)` ajoute un bouton et renvoie une poignée sur laquelle `.click { }` attache le callback exécuté au clic. Un `custom_id` est généré automatiquement — rien d'autre à câbler.

```kotlin
```
{ src="interactionButton.kt"}

Depuis un callback de clic, on peut :

respond
: Envoyer un **nouveau** message, sans toucher au message d'origine.
{type="medium"}

update
: **Modifier** le message qui portait le bouton.
{type="medium"}

> Le style `ButtonStyle.LINK` ouvre une URL et n'émet aucune interaction : `.click { }` y est sans effet.
>
{style="note"}

## Réagir à un bouton hors d'une commande

Le `.click { }` inline suffit pour un bouton envoyé en réponse à une interaction. Mais on peut aussi attacher un bouton à un **message classique** (`sendMessage`) et enregistrer son handler **au niveau du client**, via un `custom_id` stable. Utile pour des boutons persistants (menus de rôle, panneaux de config…).

```kotlin
```
{ src="interactionComponentTopLevel.kt"}

> Un `custom_id` explicite et stable rend le bouton **persistant** : il fonctionne même après un redémarrage, à condition d'avoir ré-enregistré le handler correspondant au démarrage.
>
{style="tip"}

## Réponses différées

Discord exige un accusé de réception sous **3 secondes**. Si le traitement est plus long, on appelle `defer()` pour acquitter tout de suite, puis `editOriginal { }` pour renseigner la réponse une fois le travail terminé.

```kotlin
```
{ src="interactionDefer.kt"}

## Message éphémère

Un message de réponse peut être rendu **éphémère** (visible uniquement par l'utilisateur qui a déclenché l'interaction) via `ephemeral()`.

```kotlin
```
{ src="interactionEphemeral.kt"}

## Verbes de réponse disponibles

Selon l'origine de l'interaction, le handler expose des verbes différents :

| Verbe            | Commande (`on`) | Clic sur bouton | Effet                                              |
|------------------|:---------------:|:---------------:|----------------------------------------------------|
| `respond { }`    |        ✅        |        ✅        | Envoie un nouveau message                          |
| `defer()`        |        ✅        |        ✅        | Accuse réception, réponse renseignée plus tard     |
| `editOriginal { }` |      ✅        |        ✅        | Modifie le message de réponse initial              |
| `update { }`     |        ❌        |        ✅        | Modifie le message auquel appartient le composant  |
