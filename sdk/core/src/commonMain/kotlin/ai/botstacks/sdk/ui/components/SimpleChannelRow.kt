package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.utils.IPreviews
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SimpleChannelRow(
    modifier: Modifier = Modifier,
    chat: Chat,
    showMemberPreview: Boolean = false,
    titleFontStyle: FontStyle = BotStacks.fonts.body1,
    titleColor: Color = BotStacks.colorScheme.onBackground,
    subtitleFontStyle: FontStyle = BotStacks.fonts.body1,
    subtitleColor: Color = BotStacks.colorScheme.caption,

    onClick: () -> Unit = { },
) {
    SimpleChannelRow(
        modifier = modifier,
        imageUrls = listOf(chat.image),
        title = chat.displayName,
        subtitle = chat.members.take(4).joinToString { it.user.displayNameFb.split(" ").first() }.takeIf { showMemberPreview },
        titleFontStyle = titleFontStyle,
        titleColor = titleColor,
        subtitleFontStyle = subtitleFontStyle,
        subtitleColor = subtitleColor,
        onClick = onClick
    )
}

@Composable
fun SimpleChannelRow(
    modifier: Modifier,
    imageUrls: List<String?>,
    title: String,
    titleFontStyle: FontStyle = BotStacks.fonts.body1,
    titleColor: Color = BotStacks.colorScheme.onBackground,
    subtitle: String? = null,
    subtitleFontStyle: FontStyle = BotStacks.fonts.body1,
    subtitleColor: Color = BotStacks.colorScheme.caption,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Avatar(type = AvatarType.Channel(imageUrls))
        Column {
            Text(
                text = title,
                fontStyle = titleFontStyle,
                color = titleColor,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontStyle = subtitleFontStyle,
                    color = subtitleColor,
                )
            }
        }
    }
}

@IPreviews
@Composable
fun ChannelRow_Preview() {
    BotStacksChatContext {
        Column(Modifier.background(BotStacks.colorScheme.background)) {
            SimpleChannelRow(
                modifier = Modifier
                    .fillMaxWidth(),
                imageUrls = listOf(null),
                title = "Channel Name",
                onClick = {}

            )
            SimpleChannelRow(
                modifier = Modifier
                    .fillMaxWidth(),
                imageUrls = listOf(null),
                title = "Channel Name",
                onClick = {}

            )
        }
    }
}