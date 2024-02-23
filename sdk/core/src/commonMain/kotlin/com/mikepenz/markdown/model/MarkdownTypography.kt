package com.mikepenz.markdown.model

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle

interface MarkdownTypography {
    val text: TextStyle
    val code: TextStyle
    val h1: TextStyle
    val h2: TextStyle
    val h3: TextStyle
    val h4: TextStyle
    val h5: TextStyle
    val h6: TextStyle
    val quote: TextStyle
    val paragraph: TextStyle
    val ordered: TextStyle
    val bullet: TextStyle
    val list: TextStyle
}

@Immutable
class DefaultMarkdownTypography(
    override val h1: TextStyle,
    override val h2: TextStyle,
    override val h3: TextStyle,
    override val h4: TextStyle,
    override val h5: TextStyle,
    override val h6: TextStyle,
    override val text: TextStyle,
    override val code: TextStyle,
    override val quote: TextStyle,
    override val paragraph: TextStyle,
    override val ordered: TextStyle,
    override val bullet: TextStyle,
    override val list: TextStyle
) : MarkdownTypography

@Composable
fun markdownTypography(
    h1: TextStyle = BotStacks.fonts.h1.asTextStyle(),
    h2: TextStyle = BotStacks.fonts.h2.asTextStyle(),
    h3: TextStyle = BotStacks.fonts.h3.asTextStyle(),
    h4: TextStyle = MaterialTheme.typography.headlineMedium,
    h5: TextStyle = MaterialTheme.typography.headlineSmall,
    h6: TextStyle = MaterialTheme.typography.titleLarge,
    text: TextStyle = BotStacks.fonts.body1.asTextStyle(),
    code: TextStyle = BotStacks.fonts.body2.copy(family = FontFamily.Monospace).asTextStyle(),
    quote: TextStyle = BotStacks.fonts.body2.asTextStyle().plus(SpanStyle(fontStyle = FontStyle.Italic)),
    paragraph: TextStyle = BotStacks.fonts.body1.asTextStyle(),
    ordered: TextStyle = BotStacks.fonts.body1.asTextStyle(),
    bullet: TextStyle = BotStacks.fonts.body1.asTextStyle(),
    list: TextStyle = BotStacks.fonts.body1.asTextStyle(),
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1, h2 = h2, h3 = h3, h4 = h4, h5 = h5, h6 = h6,
    text = text, quote = quote, code = code, paragraph = paragraph,
    ordered = ordered, bullet = bullet, list = list
)