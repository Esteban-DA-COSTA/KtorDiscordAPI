# Réagir aux événements

Les **événements** sont les notifications que Discord envoie au bot via la Gateway : connexion établie
(`ReadyEvent`), nouveau message (`MessageCreateEvent`), message modifié, salon créé, etc. La liste
complète des types disponibles est dans le [Catalogue des événements](Events_catalog.md).

On associe un *handler* à un type d'événement via `on<T>()` : la clé est le **type**, indiqué en
paramètre générique.

## Réagir à un événement

`on<T>()` enregistre un handler exécuté à chaque événement de type `T`. À l'intérieur, l'événement
reçu est accessible via la propriété `event`, typée selon `T`.

```kotlin
```
{ src="eventOn.kt"}

T
: Le type d'événement à écouter (`ReadyEvent`, `MessageCreateEvent`…). C'est lui qui sélectionne le
handler et le type de `event`.
{type="medium"}

handler
: Bloc exécuté à chaque événement de ce type.
{type="medium"}

> Enregistre tes handlers **avant** d'appeler [`login()`](Connexion.md). La boucle de distribution
> démarre dès la création du client, donc aucun événement précoce (comme `ReadyEvent`) n'est manqué —
> mais seulement si le handler est déjà enregistré.
>
{style="tip"}

> Un événement n'arrive que si l'**intent** correspondant a été demandé au `login`. Par exemple,
> `MessageCreateEvent` nécessite `GUILD_MESSAGES`, et son `content` nécessite en plus
> `MESSAGE_CONTENT`. Voir [Se connecter](Connexion.md#les-intents).
>
{style="note"}

## Répondre à un message

Depuis un handler d'événement porteur d'un message (`MessageCreateEvent`, `MessageUpdateEvent`),
`reply { }` envoie un **nouveau** message dans le salon d'origine. Il utilise le même
[builder](Message_dsl.md) que `sendMessage { }` (`content`, `embed { }`, boutons…), si bien qu'on peut
y attacher un bouton et son callback de clic **inline** avec `.click { }` — exactement comme dans une
réponse d'interaction.

```kotlin
```
{ src="eventReply.kt"}

> `reply { }` n'est disponible que pour les événements qui portent un salon (les événements de message).
> Pour les autres, on agit via `event` et les appels du client ([`sendMessage`](Rest_messages.md), etc.).
>
{style="note"}

## Plusieurs handlers pour un même événement

On peut enregistrer **plusieurs** handlers pour le même type ; ils sont tous exécutés, chacun dans sa
propre coroutine. Pratique pour séparer les responsabilités (log, modération, métier…).

```kotlin
```
{ src="eventMultiple.kt"}

> Les handlers d'un même type ne sont **pas ordonnancés** entre eux et il n'y a pas de « stop
> propagation » : chacun s'exécute indépendamment.
>
{style="warning"}
