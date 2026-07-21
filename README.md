# KtorDiscordAPI

Bibliothèque Kotlin/JVM pour écrire des bots Discord, construite sur Ktor Client (REST + Gateway WebSocket), kotlinx.serialization et kotlinx.coroutines. Cible l'API Discord **v10**.

## 📖 Documentation

**➡️ [Consulter la documentation](https://esteban-da-costa.github.io/KtorDiscordAPI/)**

Tout s'y trouve : premiers pas, configuration du client, appels REST, événements Gateway, interactions (commandes & composants) et le DSL de messages.

## Installation

Les modules sont publiés sur **GitHub Packages**. Dépendre de `core` suffit : `components` et `websocket` sont tirés en transitif.

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Esteban-DA-COSTA/KtorDiscordAPI")
        credentials {
            username = providers.gradleProperty("gpr.user").get()
            password = providers.gradleProperty("gpr.key").get()
        }
    }
}

dependencies {
    implementation("io.github.esteban-da-costa:core:1.0.0-beta")
}
```

> GitHub Packages exige un token (`read:packages`) même pour lire un package public.
> Détails et configuration Maven : voir la [documentation](https://esteban-da-costa.github.io/KtorDiscordAPI/).

## Modules

| Module | Rôle |
|---|---|
| `core` | Façade publique : `DiscordClient`, appels REST. Ré-exporte `components` et `websocket`. |
| `websocket` | Couche Gateway : session WebSocket, heartbeat, reconnexion/resume, événements. |
| `components` | Modèles de données Discord, enums, interactions, DSL builders. |
