# Events

Les **events** sont les notifications que Discord envoie au bot via la Gateway : connexion établie
(`ReadyEvent`), nouveau message (`MessageCreateEvent`), message modifié, salon créé, etc.

Comme pour les interactions, on associe un *handler* à un type d'event via `on(...)`. La différence :
ici la clé est le **type** de l'event, indiqué en paramètre générique.

## Réagir à un event

`on<T>()` enregistre un handler exécuté à chaque event de type `T`. À l'intérieur, l'event reçu est
accessible via la propriété `event`, typée selon `T`.

```kotlin
```
{ src="eventOn.kt"}

T
: Le type d'event à écouter (`ReadyEvent`, `MessageCreateEvent`…). C'est lui qui sélectionne le
handler et le type de `event`.
{type="medium"}

handler
: Bloc exécuté à chaque event de ce type.
{type="medium"}

> Enregistre tes handlers **avant** d'appeler `login()`. La boucle de distribution démarre dès la
> création du client, donc aucun event précoce (comme `ReadyEvent`) n'est manqué — mais seulement si
> le handler est déjà enregistré.
>
{style="tip"}

## Répondre à un message

Depuis un handler d'event porteur d'un message (`MessageCreateEvent`, `MessageUpdateEvent`),
`reply { }` envoie un **nouveau** message dans le salon d'origine. Il utilise le même builder que
`sendMessage { }` (`content`, `embed { }`, boutons…), si bien qu'on peut y attacher un bouton et son
callback de clic **inline** avec `.click { }` — exactement comme dans une réponse d'interaction.

```kotlin
```
{ src="eventReply.kt"}

> `reply { }` n'est disponible que pour les events qui portent un salon (les events de message). Pour
> les autres events, on agit via `event` et les appels du client (`sendMessage`, etc.).
>
{style="note"}

## Plusieurs handlers pour un même event

On peut enregistrer **plusieurs** handlers pour le même type d'event ; ils sont tous exécutés, chacun
dans sa propre coroutine. Pratique pour séparer les responsabilités (log, modération, métier…).

```kotlin
```
{ src="eventMultiple.kt"}

> Les handlers d'un même type ne sont **pas ordonnancés** entre eux et il n'y a pas de « stop
> propagation » : chacun s'exécute indépendamment.
>
{style="warning"}
