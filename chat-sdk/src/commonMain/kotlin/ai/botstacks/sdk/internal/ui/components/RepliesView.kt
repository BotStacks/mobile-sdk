/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.state.usernames
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.genRepliesMessage
import ai.botstacks.sdk.ui.components.ChatMessage

@Composable
internal fun RepliesView(
    message: Message,
    modifier: Modifier = Modifier,
    onPress: (Message) -> Unit,
    onPressUser: (User) -> Unit
) {
    Column {
        Column(
            modifier = modifier
                .clickable { onPress(message) }
                .padding(16.dp, 12.dp, 0.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "#${message.chat.name ?: ""}", fontStyle = fonts.h3, color = colorScheme.onBackground)
            Text(
                text = message.chat.members.map { it.user }.usernames(),
                fontStyle = fonts.body1,
                color = colorScheme.caption
            )
            for (msg in message.replies.items) {
                ChatMessage(message = msg, onPressUser = onPressUser, onLongPress = {})
            }
            Space(24f)
        }
        Spacer(
            modifier = Modifier
                .background(colorScheme.border.copy(alpha = 0.5f))
                .height(20.dp)
                .fillMaxWidth()
        )
    }
}


@IPreviews
@Composable
private fun RepliesViewPreview() {
    BotStacksThemeEngine {
        RepliesView(message = genRepliesMessage(), onPress = {}, onPressUser = {})
    }
}