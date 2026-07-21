# Sérialisation

La sérialisation JSON est le pattern central du projet. Cette page décrit les conventions à suivre
pour ajouter un modèle ou un événement.

## Conventions

- **snake_case → camelCase** champ par champ, via `@SerialName("channel_id")` (pas de naming strategy
  globale).
- **IDs en `Snowflake`** partout, avec un sérialiseur custom. Les non-snowflakes (tokens, `custom_id`,
  bitfields de permissions, `session_id`) restent en `String`/`Int`.
- Champs optionnels : `var` **nullable** avec défaut `null`.
- Champs non sérialisés : `@Transient` (ex. `DispatchEvent.sequenceId`).

## Le sérialiseur custom maison

Le pattern récurrent est un **`companion object Serializer`** implémentant `KSerializer`. Les enums
codés par entier utilisent `PrimitiveKind.INT` :

```kotlin
```
{ src="advEnumSerializer.kt"}

> Ne réplique pas le pattern `entries[decodeInt() - 1]` (utilisé par `InteractionTypes`) : il ne
> marche que parce que les ids sont contigus à partir de 1. Préfère `entries.first { it.id == ... }`,
> comme `OPCodeSerializer`.
>
{style="warning"}

## Polymorphisme manuel

Il n'y a **pas** de `SerializersModule` ni de `classDiscriminator`. Les hiérarchies scellées sont
décodées **à la main** :

- La hiérarchie `Event` : `EventSerializer` lit l'enveloppe `{op, t, s, d}` en `JsonElement`, puis
  dispatche (`op == DISPATCH` → switch sur `DispatchEvents`, sinon switch sur `OPCode`) et décode `d`
  avec le bon sérialiseur.
- `InteractionData` : même approche, switch sur le `type`.

## Deux configurations JSON

Le `DiscordClient` configure **deux** instances `Json` :

| Config | Options | Pourquoi |
|---|---|---|
| REST | `ignoreUnknownKeys`, `coerceInputValues` | Tolérer les champs inconnus renvoyés par Discord |
| WebSocket | `explicitNulls = false`, `isLenient`, `encodeDefaults` | Coller au format d'enveloppe Gateway |

## Ajouter un événement Gateway

1. Ajouter le nom dans l'enum `gateway/DispatchEvents.kt`.
2. Créer la classe dans `websocket/src/gateway/events/` (hériter de `DispatchEvent`,
   `override var sequenceId`, `@Serializable`).
3. Câbler le décodage dans le `when` de `EventSerializer.decodeDispatchEvent` (`Event.kt`).

## Ajouter un modèle

Nouveau fichier dans `components/src/components/`, `data class @Serializable`, `@SerialName` pour chaque
champ snake_case, `Snowflake` pour les ids, champs optionnels nullables à `null`.
