package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.internal.utils.IPreviews
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

/**
 * ChannelRow
 *
 * Renders a given [Chat] in a Row.
 *
 * This is a convenience wrapper around [Chat] to handle extract the default information to display.
 *
 * This is utilized in the [ChannelGroup] within [UserDetailsView] to show channels the current user has in common with
 * any other user.
 *
 * @param modifier the Modifier to be applied to this ChannelRow
 * @param chat The channel to display
 * @param showMemberPreview Whether to show a member preview as the subtitle.
 * @param titleFontStyle [FontStyle] for the title (top text).
 * @param titleColor [Color] for the title.
 * @param subtitleFontStyle [FontStyle] for the subtitle (bottom text).
 * @param subtitleColor [Color] for the subtitle.
 * @param onClick called when this button is clicked
 *
 */
@Composable
fun ChannelRow(
    modifier: Modifier = Modifier,
    chat: Chat,
    showMemberPreview: Boolean = false,
    titleFontStyle: FontStyle = BotStacks.fonts.body1,
    titleColor: Color = BotStacks.colorScheme.onBackground,
    subtitleFontStyle: FontStyle = BotStacks.fonts.body1,
    subtitleColor: Color = BotStacks.colorScheme.caption,
    onClick: () -> Unit = { },
) {
    ChannelRow(
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

/**
 * ChannelRow
 *
 * Used to render information about a [Chat].
 *
 * This is utilized in the [ChannelGroup] within [UserDetailsView] to show channels the current user has in common with
 * any other user.
 *
 * @param modifier the Modifier to be applied to this ChannelRow
 * @param imageUrls [User] image urls of members of the channel.
 * @param title Text to display in title slot (top text).
 * @param titleFontStyle [FontStyle] for the title (top text).
 * @param titleColor [Color] for the title.
 * @param subtitle Optional text to display in subtitle slot (bottom text).
 * @param subtitleFontStyle [FontStyle] for the subtitle (bottom text).
 * @param subtitleColor [Color] for the subtitle.
 * @param onClick called when this button is clicked
 *
 */
@Composable
fun ChannelRow(
    modifier: Modifier = Modifier,
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
private fun ChannelRow_Preview() {
    BotStacksThemeEngine {
        Column(Modifier.background(BotStacks.colorScheme.background)) {
            ChannelRow(
                modifier = Modifier
                    .fillMaxWidth(),
                imageUrls = listOf(null),
                title = "Channel Name",
                onClick = {}

            )
            ChannelRow(
                modifier = Modifier
                    .fillMaxWidth(),
                imageUrls = listOf(null),
                title = "Channel Name",
                onClick = {}

            )
        }
    }
}