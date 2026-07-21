# Créer &amp; configurer le client

Le `DiscordClient` est le point d'entrée unique de la bibliothèque : il porte les appels REST,
distribue les événements et route les interactions. On le construit avec `DiscordClient.create`.

## Créer le client

```kotlin
```
{ src="clientCreate.kt"}

token
: Le token d'authentification du bot Discord.
{type="medium"}

componentCacheSize
: Taille max (LRU) du cache des handlers de composants **éphémères** (ceux générés par
`button(...).click { }`). Défaut : `1024`.
{type="medium"}

componentCacheTtlMs
: Durée de vie d'un handler éphémère avant éviction, en millisecondes. Défaut : `900000` (15 min).
{type="medium"}

> `create` est **suspendue** et **bloquante à la construction** : elle appelle Discord pour résoudre
> l'`applicationId` du bot. Un token invalide ou l'absence de réseau fait échouer la création.
>
{style="note"}

## Le cache de composants éphémères
{ id="le-cache-de-composants-ephemeres"}

Deux chemins existent pour router un clic de bouton (voir [Composants &amp; boutons](Components.md)) :

- **Déclaré / persistant** : `on(InteractionKind.Component, "prefix") { }` — registre non borné, survit
  au redémarrage.
- **Éphémère** : `button(...).click { }` — la closure est stockée dans un cache **borné (LRU + TTL)**,
  d'où les paramètres `componentCacheSize` / `componentCacheTtlMs`.

Augmente ces bornes si tu émets beaucoup de boutons éphémères simultanément et que certains clics
tardifs ne sont plus routés (handler évincé). Pour des boutons qui doivent survivre à un redémarrage,
préfère le chemin **déclaré** plutôt que d'allonger le TTL.

## Fermer le client

`close()` annule toutes les coroutines détenues par le client (boucle Gateway, heartbeat) et libère le
client HTTP sous-jacent.

```kotlin
```
{ src="clientClose.kt"}

> Après `close()`, l'instance **n'est plus réutilisable** : recrée un client si nécessaire.
>
{style="warning"}

## Deux configurations JSON

En interne, le client utilise **deux configurations de sérialisation distinctes** — une pour le REST
(`ignoreUnknownKeys`, `coerceInputValues`) et une pour la Gateway (`explicitNulls = false`,
`isLenient`). C'est un détail d'implémentation, mais il explique pourquoi des champs inconnus renvoyés
par Discord n'interrompent pas la désérialisation. Voir [Sérialisation](Adv_serialization.md).
