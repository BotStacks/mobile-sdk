package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.markdownColors
import com.mikepenz.markdown.model.markdownTypography

@Composable
fun MarkdownView(modifier: Modifier = Modifier, content: String, isCurrentUser: Boolean) {
    Markdown(
        modifier = modifier,
        typography = markdownTypography(
            text = BotStacks.fonts.body1
                .asTextStyle()
                .copy(color = if (isCurrentUser) BotStacks.colorScheme.onPrimary else BotStacks.colorScheme.onMessage,)
        ),
        colors = markdownColors(
            text = if (isCurrentUser) BotStacks.colorScheme.onPrimary else BotStacks.colorScheme.onMessage,
            linkText = if (isCurrentUser) BotStacks.colorScheme.onPrimary else BotStacks.colorScheme.primary,
        ),
        content = content
    )
}