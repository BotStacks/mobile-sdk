package com.mikepenz.markdown.model

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
interface MarkdownColors {
    /** Represents the color used for the text of this [Markdown] component. */
    val text: Color

    /** Represents the color used for the text of code. */
    val codeText: Color

    /** Represents the color used for the text of links. */
    val linkText: Color

    /** Represents the color used for the background of code. */
    val codeBackground: Color

    /** Represents the color used for the inline background of code. */
    val inlineCodeBackground: Color

    /** Represents the color used for the color of dividers. */
    val dividerColor: Color
}

@Immutable
class DefaultMarkdownColors(
    override val text: Color,
    override val codeText: Color,
    override val linkText: Color,
    override val codeBackground: Color,
    override val inlineCodeBackground: Color,
    override val dividerColor: Color,
) : MarkdownColors


@Composable
fun markdownColors(
    text: Color = BotStacks.colorScheme.onMessage,
    codeText: Color = Color.White,
    linkText: Color = BotStacks.colorScheme.primary,
    codeBackground: Color = Color.Gray,
    inlineCodeBackground: Color = codeBackground,
    dividerColor: Color = BotStacks.colorScheme.caption,
): MarkdownColors = DefaultMarkdownColors(
    text,
    codeText,
    linkText,
    codeBackground,
    inlineCodeBackground,
    dividerColor,
)