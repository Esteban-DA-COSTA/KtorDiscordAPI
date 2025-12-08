package builders

import components.*
import java.awt.Color

@DslMarker
annotation class DiscordMessageBuilder

//#region Message builder
@DiscordMessageBuilder
fun Message.content(content: String) {
    this.content = content
}

@DiscordMessageBuilder
fun Message.embed(init: Embed.() -> Unit) {
    if (this.embeds == null)
        this.embeds = mutableListOf()
    this.embeds!!.add(Embed().apply(init))
}
//#endregion

//#region Embed builder
@DiscordMessageBuilder
fun Embed.title(title: String) {
    this.title = title
}

@DiscordMessageBuilder
fun Embed.type(type: components.enums.EmbedTypes) {
    this.type = type
}

@DiscordMessageBuilder
fun Embed.description(description: String) {
    this.description = description
}

@DiscordMessageBuilder
fun Embed.url(url: String) {
    this.url = url
}

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

//#region EmbedFooter builder
@DiscordMessageBuilder
fun EmbedFooter.text(text: String) {
    this.text = text
}

@DiscordMessageBuilder
fun EmbedFooter.iconUrl(url: String) {
    this.iconUrl = url
}
//#endregion

//#region EmbedImage builder
@DiscordMessageBuilder
fun EmbedImage.url(url: String) {
    this.url = url
}

@DiscordMessageBuilder
fun EmbedImage.height(height: Int) {
    this.height = height
}

@DiscordMessageBuilder
fun EmbedImage.width(width: Int) {
    this.width = width
}
//#endregion

//#region EmbedProvider builder
@DiscordMessageBuilder
fun EmbedProvider.name(name: String) {
    this.name = name
}

@DiscordMessageBuilder
fun EmbedProvider.url(url: String) {
    this.url = url
}
//#endregion

//#region EmbedAuthor builder
@DiscordMessageBuilder
fun EmbedAuthor.name(name: String) {
    this.name = name
}

@DiscordMessageBuilder
fun EmbedAuthor.url(url: String) {
    this.url = url
}

@DiscordMessageBuilder
fun EmbedAuthor.iconUrl(url: String) {
    this.url = url
}
//#endregion