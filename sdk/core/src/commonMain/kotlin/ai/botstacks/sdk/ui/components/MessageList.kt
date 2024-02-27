/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.internal.navigation.BackHandler
import ai.botstacks.sdk.internal.ui.components.EmptyListView
import ai.botstacks.sdk.internal.ui.components.ImageRenderer
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Identifiable
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Pager
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.assets
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacks.shapes
import ai.botstacks.sdk.internal.ui.components.PagerListIndexed
import ai.botstacks.sdk.internal.utils.format
import ai.botstacks.sdk.internal.utils.ift
import ai.botstacks.sdk.internal.utils.minutesBetween
import ai.botstacks.sdk.type.AttachmentType
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    chat: Chat,
    onPressUser: (User) -> Unit,
    onLongPress: (Message) -> Unit
) {
    var attachmentToView by remember {
        mutableStateOf<FMessage.Attachment?>(null)
    }

    val pager = chat as Pager<Identifiable>
    PagerListIndexed(
        pager = pager,
        prefix = chat.sending,
        modifier = modifier,
        scrollToTop = chat.sending.firstOrNull()?.id ?: chat.items.firstOrNull()?.id,
        invert = true,
        canRefresh = false,
        separator = { before, after ->
            val dateBefore = (before as? Message)?.createdAt?.format("MMM dd")
            val dateAfter = (after as? Message)?.createdAt?.format("MMM dd")

            if (dateBefore != dateAfter) {
                dateBefore?.let {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(bottom = dimens.grid.x4),
                        contentAlignment = Alignment.Center
                    ) {
                        Badge(
                            label = it,
                            backgroundColor = colorScheme.message,
                            contentColor = colorScheme.onMessage,
                            shape = shapes.small,
                        )
                    }
                }
            }
        },
        contentPadding = PaddingValues(dimens.inset),
        empty = { EmptyListView(config = assets.emptyChat) }
    ) { index, item ->
        if (item is Message) {
            val previousMessage = pager.items.getOrNull(index + 1)
                ?.let { it as? Message }
                ?.takeIf { it.user == item.user }

            val nextMessage = pager.items.getOrNull(index - 1)
                ?.let { it as? Message }
                ?.takeIf { it.user == item.user }

            val isPrevClose = previousMessage
                ?.takeIf { it.createdAt.minutesBetween(item.createdAt) in 0 until 1 } != null

            val isNextClose = nextMessage
                ?.takeIf { it.createdAt.minutesBetween(item.createdAt) in 0 until 1 } != null

            val arrangement = when {
                isNextClose && isPrevClose -> PaddingValues(vertical = dimens.grid.x1 / 2)
                isNextClose -> PaddingValues(bottom = dimens.grid.x1 / 2, top = dimens.grid.x2)
                isPrevClose -> PaddingValues(top = dimens.grid.x1 / 2, bottom = dimens.grid.x2)
                else -> PaddingValues(vertical = dimens.grid.x2)
            }

            ChatMessage(
                modifier = Modifier.padding(arrangement),
                message = item,
                shape = shapeForMessage(item.user.isCurrent, isPrevClose, isNextClose),
                showTimestamp = !isNextClose,
                showAvatar = !isNextClose,
                onPressUser = onPressUser,
                onClick = { attachmentToView = it },
                onLongPress = {
                    onLongPress(item)
                },
            )
        }
    }

    AnimatedVisibility(attachmentToView != null) {
        attachmentToView?.let { attachment ->
            when (attachment.type) {
                AttachmentType.image -> {
                    Popup(Alignment.Center, onDismissRequest = { attachmentToView = null }) {
                        ImageRenderer(
                            url = attachment.url,
                            contentDescription = "shared image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onClick = { attachmentToView = null }
                        )
                    }
                    BackHandler(true) {
                        attachmentToView = null
                    }
                }
                AttachmentType.video -> Unit
                else -> Unit
            }
        }
    }
}

@Composable
private fun shapeForMessage(
    isCurrentUser: Boolean,
    isPrevClose: Boolean,
    isNextClose: Boolean
): CornerBasedShape {
    return if (isCurrentUser) {
        when {
            isNextClose && isPrevClose -> shapes.medium.copy(
                topEnd = CornerSize(2.dp),
                bottomEnd = CornerSize(2.dp)
            )

            isNextClose -> shapes.medium.copy(bottomEnd = CornerSize(2.dp))
            isPrevClose -> shapes.medium.copy(topEnd = CornerSize(2.dp))
            else -> shapes.medium
        }
    } else {
        when {
            isNextClose && isPrevClose -> shapes.medium.copy(
                topStart = CornerSize(2.dp),
                bottomStart = CornerSize(2.dp)
            )

            isNextClose -> shapes.medium.copy(bottomStart = CornerSize(2.dp))
            isPrevClose -> shapes.medium.copy(topStart = CornerSize(2.dp))
            else -> shapes.medium
        }
    }
}

//@Composable
//fun SendingMessageView(message: SendingMessage, isGroup: Boolean) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable {
//                if (message.failed) {
//                    message.retry()
//                }
//            },
//        horizontalAlignment = Alignment.End
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.End
//        ) {
//            Row(modifier = Modifier.fillMaxWidth(0.85f)) {
//                MessageView(
//                    message = message.msg,
//                    isGroup = isGroup,
//                    onPressUser = {},
//                    onLongPress = {},
//                    onClick = {
//                        if (message.failed) {
//                            message.retry()
//                        }
//                    })
//            }
//            if (message.failed) {
//                Icon(
//                    imageVector = Icons.Outlined.Refresh,
//                    contentDescription = "Retry sending message",
//                    tint = Color.Red,
//                    modifier = Modifier.size(30.dp)
//                )
//            } else {
//                Spinner()
//            }
//        }
//        if (message.failed) {
//            Text(
//                text = "The message failed to send. Tap to retry.",
//                fontStyle = fonts.body1,
//                color = Color.Red
//            )
//        }
//    }
//}