/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.navigation.BackHandler
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.ui.components.EmptyListView
import ai.botstacks.sdk.internal.ui.components.ImageRenderer
import ai.botstacks.sdk.internal.ui.components.PagerListIndexed
import ai.botstacks.sdk.internal.ui.components.ScrollStartPosition
import ai.botstacks.sdk.internal.ui.utils.HandleMessageChanges
import ai.botstacks.sdk.internal.utils.format
import ai.botstacks.sdk.internal.utils.minutesBetween
import ai.botstacks.sdk.state.AttachmentType
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.MessageAttachment
import ai.botstacks.sdk.state.Pager
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.assets
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacks.shapes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

/**
 * MessageList
 *
 * A conversational component displaying the messages for a given [Chat] in an infinite scrolling list.
 *
 * @param modifier the Modifier to be applied to this list.
 * @param chat The chat to render messages for.
 * @param onPressUser callback when a user's avatar is pressed when visible next to a given message.
 * (only applicable in multi user based chats).
 * @param onLongPress callback when a message is long pressed. This can be utilized with [MessageActionSheet] to show contextual actions.
 *
 */
@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    chat: Chat,
    header: @Composable () -> Unit = { },
    contentHeader: (@Composable () -> Unit)? = null,
    initialPosition: ScrollStartPosition = ScrollStartPosition.None,
    emptyState: @Composable () -> Unit = { EmptyListView(config = assets.emptyChat) },
    openThread: (Message) -> Unit,
    onPressUser: (User) -> Unit,
    onLongPress: (Message) -> Unit,
) {

    val pager = remember(chat.id) { chat }

    MessageList(
        modifier = modifier,
        pager = pager,
        prefix = chat.sending,
        header = header,
        contentHeader = contentHeader,
        initialPosition = initialPosition,
        emptyState = emptyState,
        onPressUser = onPressUser,
        onLongPress = onLongPress,
        openThread = openThread,
    )
}

@Composable
fun ThreadMessageList(
    modifier: Modifier = Modifier,
    message: Message,
    header: @Composable () -> Unit = { },
    contentHeader: (@Composable () -> Unit)? = null,
    initialPosition: ScrollStartPosition = ScrollStartPosition.Beginning,
    emptyState: @Composable () -> Unit = { EmptyListView(config = assets.emptyChat) },
    onPressUser: (User) -> Unit,
    onLongPress: (Message) -> Unit,
) {

    val pager = remember(message.id) {
        BotStacksChatStore.current.repliesFor(message.id)
    }

    println("c=${message.chat.sending.count()}")
    MessageList(
        modifier = modifier,
        pager = pager,
        prefix = message.chat.sending,
        header = header,
        contentHeader = contentHeader,
        initialPosition = initialPosition,
        emptyState = emptyState,
        onPressUser = onPressUser,
        onLongPress = onLongPress,
        showReplies = false,
    )
}

@Composable
internal fun MessageList(
    modifier: Modifier = Modifier,
    pager: Pager<Message>,
    prefix: List<Message> = emptyList(),
    header: @Composable () -> Unit = { },
    contentHeader: (@Composable () -> Unit)? = null,
    initialPosition: ScrollStartPosition = ScrollStartPosition.None,
    emptyState: @Composable () -> Unit = { EmptyListView(config = assets.emptyChat) },
    showReplies: Boolean = true,
    openThread: ((Message) -> Unit)? = null,
    onPressUser: (User) -> Unit,
    onLongPress: (Message) -> Unit,
) {
    var attachmentToView by remember {
        mutableStateOf<MessageAttachment?>(null)
    }

    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    var scrolled by remember(pager.id) { mutableStateOf(false) }
    LaunchedEffect(initialPosition, pager.loading, pager.items, pager.hasMore) {
        if (scrolled) return@LaunchedEffect
        if (pager.loading) return@LaunchedEffect
        if (pager.hasMore) return@LaunchedEffect
        if (pager.items.isEmpty()) return@LaunchedEffect
        when (initialPosition) {
            ScrollStartPosition.BeginningAnimated -> listState.animateScrollToItem(pager.items.lastIndex)
            ScrollStartPosition.Beginning -> listState.scrollToItem(pager.items.lastIndex)
            ScrollStartPosition.EndAnimated -> listState.animateScrollToItem(0)
            ScrollStartPosition.End -> listState.scrollToItem(0)
            ScrollStartPosition.None -> Unit
        }
        scrolled = true
    }

    HandleMessageChanges(listState = listState, prefix = prefix, pager = pager)

    PagerListIndexed(
        pager = pager,
        listState = listState,
        header = header,
        contentHeader = contentHeader,
        prefix = prefix,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        invert = true,
        canRefresh = false,
        separator = { before, after ->
            val dateBefore = before?.createdAt?.format("MMM dd")
            val dateAfter = after?.createdAt?.format("MMM dd")

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
        empty = emptyState,
    ) { index, item ->
        val previousMessage = pager.items.getOrNull(index + 1)
            ?.takeIf { it.userOrNull == item.userOrNull && item.userOrNull != null && it.userOrNull != null }

        val nextMessage = pager.items.getOrNull(index - 1)
            ?.takeIf { it.userOrNull == item.userOrNull && item.userOrNull != null && it.userOrNull != null }

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
            shape = shapeForMessage(item.userOrNull?.isCurrent == true, isPrevClose, isNextClose),
            showTimestamp = !isNextClose,
            showAvatar = !isNextClose,
            onPressUser = onPressUser,
            onClick = { attachmentToView = it },
            onLongPress = {
                onLongPress(item)
            },
            showReplies = showReplies,
            openThread = { openThread?.invoke(item) },
        )
    }

    AnimatedVisibility(attachmentToView != null) {
        attachmentToView?.let { attachment ->
            when (attachment.type) {
                AttachmentType.Image -> {
                    Popup(Alignment.Center, onDismissRequest = { attachmentToView = null }) {
                        Box {
                            ImageRenderer(
                                url = attachment.url,
                                contentDescription = "shared image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                onClick = { attachmentToView = null },
                            )

                            HeaderButton(
                                modifier = Modifier.align(Alignment.TopStart)
                                    .padding(dimens.inset)
                                    .background(colorScheme.surface, CircleShape)
                                    .padding(dimens.grid.x2),
                                onClick = { attachmentToView = null },
                                icon = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "Close",
                                        tint = colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }

                    BackHandler(true) {
                        attachmentToView = null
                    }
                }

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