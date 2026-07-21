# Introduction &amp; installation

**Ktor Discord API** (KDA) est une bibliothèque Kotlin/JVM qui encapsule l'API Discord — REST **et**
Gateway WebSocket — pour écrire des bots. Elle vise l'API Discord **v10** et s'appuie sur Ktor
Client, kotlinx.serialization et kotlinx.coroutines.

## Ce que la bibliothèque fait pour toi

- **Appels REST typés** exposés directement sur le client (`sendMessage`, `getGuild`, `createGuildBan`…),
  chacun renvoyant un [`DiscordResponse<T>`](Rest_overview.md) qui porte à la fois le statut HTTP et le
  résultat désérialisé.
- **Distribution des événements** de la Gateway (`ReadyEvent`, `MessageCreateEvent`…) via un simple
  `on<T> { }`, voir [Événements](Events.md).
- **Interactions** : déclaration et gestion des [slash commands](Commands.md), et routage des clics de
  [boutons](Components.md).
- **DSL de construction** pour les [messages et embeds](Message_dsl.md).
- Gestion transparente du **rate limit** (`429`), du **heartbeat** et de la **reconnexion/resume**.

## Prérequis

- **JDK 17+** et un projet Kotlin/JVM.
- Un **bot Discord** enregistré sur le [portail développeur](https://discord.com/developers/applications),
  dont tu as récupéré le **token**.
- Les **intents** dont ton bot a besoin activés côté portail (les intents privilégiés — `MESSAGE_CONTENT`,
  `GUILD_MEMBERS`, `GUILD_PRESENCES` — doivent être cochés explicitement). Voir [Se connecter](Connexion.md).

> Ne committe jamais ton token dans le code source. Passe-le par une variable d'environnement ou un
> fichier de configuration ignoré par Git.
>
{style="warning"}

## Ajouter la dépendance

La bibliothèque est publiée sur **GitHub Packages**. Un consommateur ne dépend que de l'artefact
`kda` (le module `core`, publié sous ce nom) : il ré-exporte `components` et `websocket`.

```kotlin
// build.gradle.kts
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Esteban-DA-COSTA/KtorDiscordAPI")
        credentials {
            username = providers.gradleProperty("gpr.user").get()
            password = providers.gradleProperty("gpr.key").get() // un Personal Access Token (scope read:packages)
        }
    }
}

dependencies {
    implementation("io.github.esteban-da-costa:kda:1.0.0-beta")
}
```

> GitHub Packages **exige une authentification**, même pour un paquet public : renseigne un
> Personal Access Token disposant du scope `read:packages` (par ex. dans `~/.gradle/gradle.properties`).
>
{style="note"}

Une fois la dépendance en place, passe aux [Premiers pas](Premiers_pas.md).
