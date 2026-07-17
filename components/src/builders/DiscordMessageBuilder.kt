package ktordiscord.builders

import ktordiscord.components.*
import java.awt.Color

@DslMarker
annotation class DiscordMessageBuilder

// Only builder functions that add value over direct `var` assignment are kept:
// nested-object initializers and the java.awt.Color -> Int conversion. Every field
// that was merely mirrored by a `xxx(value)` function is set directly in the lambda
// (e.g. `embed { title = "..." }`).

//#region Message builder
@DiscordMessageBuilder
fun MessagePayload.embed(init: Embed.() -> Unit) {
    if (this.embeds == null)
        this.embeds = mutableListOf()
    this.embeds!!.add(Embed().apply(init))
}
//#endregion

//#region Embed builder
/** Set the embed color from a [java.awt.Color] (stored as its packed RGB Int). */
@DiscordMessageBuilder
fun Embed.color(color: Color) {
    this.color = color.rgb
}

@DiscordMessageBuilder
fun Embed.footer(init: EmbedFooter.() -> Unit) {
    this.footer = EmbedFooter().apply(init)
}

@DiscordMessageBuilder
fun Embed.image(init: EmbedImage.() -> Unit) {
    this.image = EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun Embed.thumbnail(init: EmbedImage.() -> Unit) {
    this.thumbnail = EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun Embed.video(init: EmbedImage.() -> Unit) {
    this.video = EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun Embed.provider(init: EmbedProvider.() -> Unit) {
    this.provider = EmbedProvider().apply(init)
}

@DiscordMessageBuilder
fun Embed.author(init: EmbedAuthor.() -> Unit) {
    this.author = EmbedAuthor().apply(init)
}

@DiscordMessageBuilder
fun Embed.field(init: EmbedField.() -> Unit) {
    if (this.fields == null)
        this.fields = mutableListOf()
    this.fields!!.add(EmbedField().apply(init))
}
//#endregion
