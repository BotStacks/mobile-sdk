/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.ui.components.ActionItem
import ai.botstacks.sdk.internal.ui.components.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.annotated
import ai.botstacks.sdk.internal.utils.genChatextMessage
import androidx.compose.ui.unit.dp
import ai.botstacks.`chat-sdk`.generated.resources.Res

internal enum class MessageAction {
    react,
    copy,
    reply,
    favorite;

    companion object {
        val supportedActions = listOf(
            copy,
            reply
        )
    }
}
/**
 * MediaActionSheetState
 *
 * A state that drives visibility of the [MessageActionSheet].
 */
@OptIn(ExperimentalMaterialApi::class)
class MessageActionSheetState(sheetState: ModalBottomSheetState? = null) : ActionSheetState(sheetState) {
    var messageForAction by mutableStateOf<Message?>(null)
}

/**
 * Creates a [MessageActionSheetState] and remembers it.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberMessageActionSheetState(message: Message? = null): MessageActionSheetState {

    val state = ActionSheetDefaults.SheetState

    return remember(message) {
        MessageActionSheetState(state).apply {
            messageForAction = message
        }
    }
}

/**
 * MessageActionSheet
 *
 * A modal bottom sheet that allows contextual actions for a given messaged. This is a top level
 * scaffold that is designed to wrap your screen content.
 *
 * This can be utilized in conjunction with [MessageList] to show contextual actions fro the [MessageListView#onLongPress] callback
 *
 * @param state the state for the ModalBottomSheet. @see [ModalBottomSheetState]
 * @param content your screen content.
 *
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MessageActionSheet(
    state: MessageActionSheetState,
    openThread: (Message) -> Unit,
    content: @Composable () -> Unit
) {
    MessageActionSheetContainer(state, openThread) { onAction ->
        ModalBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            sheetState = state.sheetState ?: ActionSheetDefaults.SheetState,
            sheetBackgroundColor = colorScheme.background,
            sheetContentColor = colorScheme.onBackground,
            scrimColor = colorScheme.scrim,
            sheetContent = {
                MessageActionSheetContent(onAction)
            },
            content = content
        )
    }
}

@Composable
internal fun MessageActionSheetContainer(
    state: MessageActionSheetState,
    openThread: (Message) -> Unit,
    content: @Composable (onSelection: (MessageAction) -> Unit) -> Unit,
) {

    val annotatedString = (state.messageForAction?.text ?: "").annotated()

    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(state.messageForAction) {
        if (state.messageForAction != null) {
            state.show()
        } else {
            state.hide()
        }
    }

    Box {
        content { action ->
            when (action) {
                MessageAction.react -> Unit
                MessageAction.copy -> {
                    clipboardManager.setText(annotatedString)
                    state.messageForAction = null
                }
                MessageAction.reply -> {
                    val message = state.messageForAction!!
                    state.messageForAction = null
                    openThread(message)
                }
                MessageAction.favorite -> Unit
            }
        }
    }
}
@Composable
internal fun MessageActionSheetContent(
    onSelection: (MessageAction) -> Unit
) {
    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
        Spacer(Modifier.height(8.dp))
        // TODO: add back once emoji keyboard is KMP
//                EmojiBar(
//                    current = message?.currentReaction,
//                    onEmoji = {
//                        message?.react(it)
//                        hide()
//                    }
//                )
        if (LocalThreaded.current.not()) {
            ActionItem(
                text = "Reply in thread",
                icon = Res.images.thread_reply_outline,
            ) {
                onSelection(MessageAction.reply)
            }

        }
//                ActionItem(
//                    text = if (message?.favorite == true) "Remove from Favorites" else "Save to Favorites",
//                    icon = Res.drawable.star_fill,
//                )
//                {
//                    message?.toggleFavorite()
//                    hide()
//                }

        ActionItem(
            text = "Copy",
            icon = Res.images.copy,
        ) {
            onSelection(MessageAction.copy)
        }
    }
}

internal val LocalThreaded = staticCompositionLocalOf { false }

@IPreviews
@Composable
private fun MessageActionSheetPreview() {
    BotStacksThemeEngine {
        val state = rememberMessageActionSheetState()

        MessageActionSheet(state, {}) {
            Button(onClick = {state.messageForAction = genChatextMessage() }) {
                Text(text = "Open Sheet", fontStyle = fonts.body2)
            }
        }
    }
}