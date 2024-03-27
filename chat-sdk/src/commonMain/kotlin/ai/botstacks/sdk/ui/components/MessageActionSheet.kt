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

/**
 * MediaActionSheetState
 *
 * A state that drives visibility of the [MessageActionSheet].
 */
@OptIn(ExperimentalMaterialApi::class)
class MessageActionSheetState(sheetState: ModalBottomSheetState) : ActionSheetState(sheetState) {
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
    state: MessageActionSheetState = rememberMessageActionSheetState(),
    content: @Composable () -> Unit
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

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = state.sheetState ?: ActionSheetDefaults.SheetState,
        sheetBackgroundColor = colorScheme.background,
        sheetContentColor = colorScheme.onBackground,
        scrimColor = colorScheme.scrim,
        sheetContent = {
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
//                ActionItem(
//                    text = "Reply in Chat",
//                    icon = Res.drawable.chat_dots,
//                ) {
//                    message?.let(onReply)
//                    hide()
//                }

//                ActionItem(
//                    text = if (message?.favorite == true) "Remove from Favorites" else "Save to Favorites",
//                    icon = Res.drawable.star_fill,
//                )
//                {
//                    message?.toggleFavorite()
//                    hide()
//                }

                ActionItem(
                    text = "Copy message text",
                    icon = Res.images.copy,
                ) {
                    clipboardManager.setText(annotatedString)
                    state.messageForAction = null
                }
            }
        },
        content = content
    )
}

@IPreviews
@Composable
private fun MessageActionSheetPreview() {
    BotStacksThemeEngine {
        val state = rememberMessageActionSheetState()

        MessageActionSheet(state) {
            Button(onClick = {state.messageForAction = genChatextMessage() }) {
                Text(text = "Open Sheet", fontStyle = fonts.body2)
            }
        }
    }
}