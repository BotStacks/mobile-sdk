/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.state.usernames
import ai.botstacks.sdk.ui.IAC.colors
import ai.botstacks.sdk.ui.IAC.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genRepliesMessage

@Composable
fun RepliesView(
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
            Text(text = "#${message.chat.name ?: ""}", iac = fonts.title3, color = colors.text)
            Text(
                text = message.chat.members.map { it.user }.usernames(),
                iac = fonts.body,
                color = colors.caption
            )
            for (message in message.replies.items) {
                MessageView(message = message, onPressUser = onPressUser, onLongPress = {})
            }
            Space(24f)
        }
        Spacer(
            modifier = Modifier
                .background(colors.bubble.copy(alpha = 0.5f))
                .height(20.dp)
                .fillMaxWidth()
        )
    }
}


@IPreviews
@Composable
fun RepliesViewPreview() {
    BotStacksChatContext {
        RepliesView(message = genRepliesMessage(), onPress = {}, onPressUser = {})
    }
}