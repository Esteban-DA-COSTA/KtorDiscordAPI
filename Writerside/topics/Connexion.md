# Se connecter à la Gateway

`login(intents)` ouvre la connexion WebSocket à la Gateway Discord. À partir de là, le bot reçoit les
[événements](Events.md) et peut mettre à jour sa [présence](Presence.md). L'appel synchronise aussi
les [commandes déclarées](Commands.md) et renvoie le `Job` de la boucle de connexion.

```kotlin
```
{ src="connexionLogin.kt"}

intents
: Le **bitfield** des intents Gateway (voir ci-dessous).
{type="medium"}

> Enregistre tous tes handlers (`on<T>`, `on(name) { }`) **avant** `login()` — la boucle de dispatch
> démarre à la création du client, mais tu ne veux manquer aucun événement précoce.
>
{style="tip"}

## Les intents

Les **intents** disent à Discord quelles familles d'événements tu veux recevoir. C'est un **bitfield** :
on combine les bits voulus avec l'opérateur `or`.

> Dans cette version, `login` prend un **`Int` brut**. Il n'existe pas encore d'enum typé pour les
> intents : tu passes directement la valeur du bit (ou la combinaison). Les valeurs ci-dessous sont
> celles de l'[API Discord](https://discord.com/developers/docs/events/gateway#gateway-intents).
>
{style="note"}

```kotlin
```
{ src="connexionIntents.kt"}

Les intents les plus courants :

| Intent | Bit (`1 shl n`) | Valeur | Événements couverts |
|---|---|---|---|
| `GUILDS` | `1 shl 0` | `1` | Création/màj de serveurs, salons, rôles |
| `GUILD_MEMBERS` * | `1 shl 1` | `2` | Arrivée/départ/màj de membres |
| `GUILD_MODERATION` | `1 shl 2` | `4` | Bans |
| `GUILD_EMOJIS_AND_STICKERS` | `1 shl 3` | `8` | Émojis, stickers |
| `GUILD_MESSAGES` | `1 shl 9` | `512` | Messages dans les serveurs |
| `GUILD_MESSAGE_REACTIONS` | `1 shl 10` | `1024` | Réactions dans les serveurs |
| `GUILD_MESSAGE_TYPING` | `1 shl 11` | `2048` | Indicateur « en train d'écrire » |
| `DIRECT_MESSAGES` | `1 shl 12` | `4096` | Messages privés |
| `MESSAGE_CONTENT` * | `1 shl 15` | `32768` | **Contenu textuel** des messages |
| `GUILD_PRESENCES` * | `1 shl 8` | `256` | Statuts/présences des membres |

`*` = **intent privilégié** : il doit être coché dans le portail développeur, sinon la connexion
échoue.

> Sans l'intent `MESSAGE_CONTENT`, le champ `content` des `MessageCreateEvent` arrive **vide** pour les
> messages qui ne mentionnent pas le bot. C'est la cause n°1 de « mon bot ne voit pas les messages ».
>
{style="warning"}

## Cycle de vie de la connexion

`login` gère seul la robustesse de la connexion : heartbeat, détection de connexion « zombie »,
reconnexion avec backoff et **resume** de session. Tu n'as rien à câbler. Le détail est décrit dans
[Cycle de vie de la Gateway](Adv_gateway.md).

Pour fermer la connexion (et tout le reste), voir [`close()`](Client.md#fermer-le-client).
