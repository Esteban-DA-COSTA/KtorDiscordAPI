# Premiers pas

Ce topic déroule un bot minimal de bout en bout : **créer le client**, **écouter un événement**,
**se connecter**. Chacun de ces gestes a ensuite son topic dédié.

## Un bot complet en quelques lignes

```kotlin
```
{ src="basicUsage.kt"}

Ce que fait cet exemple :

1. `DiscordClient.create(token)` construit le client (appel réseau — voir la note ci-dessous).
2. `on<ReadyEvent> { }` enregistre un handler exécuté à la connexion.
3. `on<MessageCreateEvent> { }` réagit à chaque message ; `reply { }` répond dans le salon d'origine.
4. `login(intents)` ouvre la connexion Gateway et bloque tant que le bot tourne.

> **Enregistre tes handlers _avant_ `login()`.** La boucle de distribution démarre dès la création du
> client : un handler posé après coup pourrait manquer les premiers événements (comme `ReadyEvent`).
>
{style="tip"}

## La construction du client est suspendue

`create` est une fonction **suspendue** : elle effectue un appel réseau à la construction (pour
résoudre l'identifiant d'application du bot). Il faut donc l'appeler depuis une coroutine — d'où le
`runBlocking { }` — avec un **token valide** et un **accès réseau**.

```kotlin
val kda = DiscordClient.create(token)
```

Le détail de la configuration (cache de composants, fermeture) est dans [Créer &amp; configurer le
client](Client.md).

## Faire un premier appel REST

En dehors de tout événement, tu peux appeler l'API à tout moment via le client. Exemple : envoyer un
message dans un salon.

```kotlin
```
{ src="basicSendMessage.kt"}

Chaque appel REST renvoie un `DiscordResponse<T>` : la façon de le traiter (succès/échec, helpers) est
détaillée dans [Interagir avec Discord](Rest_overview.md).

## La suite

<table>
    <tr><td>Objectif</td><td>Topic</td></tr>
    <tr><td>Configurer et fermer proprement le client</td><td><a href="Client.md">Créer &amp; configurer le client</a></td></tr>
    <tr><td>Choisir ses intents et se connecter</td><td><a href="Connexion.md">Se connecter à la Gateway</a></td></tr>
    <tr><td>Appeler l'API et gérer les erreurs</td><td><a href="Rest_overview.md">Interagir avec Discord</a></td></tr>
    <tr><td>Réagir aux événements</td><td><a href="Events.md">Événements</a></td></tr>
    <tr><td>Ajouter des slash commands</td><td><a href="Commands.md">Commandes d'application</a></td></tr>
</table>
