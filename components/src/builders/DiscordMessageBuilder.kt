package builders

import java.awt.Color

@DslMarker
annotation class DiscordMessageBuilder

//#region Message builder
@DiscordMessageBuilder
fun components.Message.content(content: String) {
    this.content = content
}

@DiscordMessageBuilder
fun components.Message.embed(init: components.Embed.() -> Unit) {
    if (this.embeds == null)
        this.embeds = mutableListOf()
    this.embeds!!.add(components.Embed().apply(init))
}
//#endregion

//#region Embed builder
@DiscordMessageBuilder
fun components.Embed.title(title: String) {
    this.title = title
}

@DiscordMessageBuilder
fun components.Embed.type(type: components.enums.EmbedTypes) {
    this.type = type
}

@DiscordMessageBuilder
fun components.Embed.description(description: String) {
    this.description = description
}

@DiscordMessageBuilder
fun components.Embed.url(url: String) {
    this.url = url
}

@DiscordMessageBuilder
fun components.Embed.color(color: Color) {
    this.color = color.rgb
}

@DiscordMessageBuilder
fun components.Embed.footer(init: components.EmbedFooter.() -> Unit) {
    this.footer = components.EmbedFooter().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.image(init: components.EmbedImage.() -> Unit) {
    this.image = components.EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.thumbnail(init: components.EmbedImage.() -> Unit) {
    this.thumbnail = components.EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.video(init: components.EmbedImage.() -> Unit) {
    this.video = components.EmbedImage().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.provider(init: components.EmbedProvider.() -> Unit) {
    this.provider = components.EmbedProvider().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.author(init: components.EmbedAuthor.() -> Unit) {
    this.author = components.EmbedAuthor().apply(init)
}

@DiscordMessageBuilder
fun components.Embed.field(init: components.EmbedField.() -> Unit) {
    if (this.fields == null)
        this.fields = mutableListOf()
    this.fields!!.add(components.EmbedField().apply(init))
}
//#endregion

//#region EmbedFooter builder
@DiscordMessageBuilder
fun components.EmbedFooter.text(text: String) {
    this.text = text
}

@DiscordMessageBuilder
fun components.EmbedFooter.iconUrl(url: String) {
    this.iconUrl = url
}
//#endregion

//#region EmbedImage builder
@DiscordMessageBuilder
fun components.EmbedImage.url(url: String) {
    this.url = url
}

@DiscordMessageBuilder
fun components.EmbedImage.height(height: Int) {
    this.height = height
}

@DiscordMessageBuilder
fun components.EmbedImage.width(width: Int) {
    this.width = width
}
//#endregion

//#region EmbedProvider builder
@DiscordMessageBuilder
fun components.EmbedProvider.name(name: String) {
    this.name = name
}

@DiscordMessageBuilder
fun components.EmbedProvider.url(url: String) {
    this.url = url
}
//#endregion

//#region EmbedAuthor builder
@DiscordMessageBuilder
fun components.EmbedAuthor.name(name: String) {
    this.name = name
}

@DiscordMessageBuilder
fun components.EmbedAuthor.url(url: String) {
    this.url = url
}

@DiscordMessageBuilder
fun components.EmbedAuthor.iconUrl(url: String) {
    this.url = url
}
//#endregion