/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.actions.toggleFavorite
import ai.botstacks.sdk.internal.ui.components.ActionItemDefaults
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.annotated
import ai.botstacks.sdk.internal.utils.genChatextMessage
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp

enum class MessageAction {
    favorite,
    forward,
    reply,
    edit,
    copy,
    delete;

    internal companion object {
        val supportedActions = listOf(
            favorite,
            reply,
            copy,
        )
    }
}

/**
 * MediaActionSheetState
 *
 * A state that drives visibility of the [MessageActionSheet].
 */
@OptIn(ExperimentalMaterialApi::class)
class MessageActionSheetState(
    sheetState: ModalBottomSheetState? = null,
) : ActionSheetState(sheetState) {
    var messageForAction by mutableStateOf<Message?>(null)
    var onAction by mutableStateOf<((Message, MessageAction) -> Unit)?>(null)
}

/**
 * Creates a [MessageActionSheetState] and remembers it.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberMessageActionSheetState(
    message: Message? = null,
    onAction: (message: Message, MessageAction) -> Unit = { _, _ -> },
): MessageActionSheetState {

    val state = ActionSheetDefaults.SheetState

    return remember(message) {
        MessageActionSheetState(state).apply {
            messageForAction = message
            this.onAction = onAction
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
    content: @Composable () -> Unit
) {
    MessageActionSheetContainer(state) { onAction ->
        ModalBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            sheetState = state.sheetState ?: ActionSheetDefaults.SheetState,
            sheetBackgroundColor = colorScheme.background,
            sheetContentColor = colorScheme.onBackground,
            scrimColor = colorScheme.scrim,
            sheetContent = {
                MessageActionSheetContent(state.messageForAction, onAction)
            },
            content = content
        )
    }
}

@Composable
internal fun MessageActionSheetContainer(
    state: MessageActionSheetState,
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
            state.onAction?.invoke(state.messageForAction!!, action)
            when (action) {
                MessageAction.copy -> {
                    clipboardManager.setText(annotatedString)
                }

                MessageAction.reply -> Unit
                MessageAction.favorite -> {
                    state.messageForAction?.toggleFavorite()
                }
                MessageAction.forward -> Unit
                MessageAction.edit -> Unit
                MessageAction.delete -> Unit
            }
            state.messageForAction = null
        }
    }
}

@Composable
internal fun MessageActionSheetContent(
    message: Message?,
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
        val items = ActionItemDefaults.messageItems(message, onSelection)
        items.forEach { it() }
    }
}

internal val LocalThreaded = staticCompositionLocalOf { false }

@IPreviews
@Composable
private fun MessageActionSheetPreview() {
    BotStacksThemeEngine {
        val state = rememberMessageActionSheetState { _, _ -> }

        MessageActionSheet(state) {
            Button(onClick = { state.messageForAction = genChatextMessage() }) {
                Text(text = "Open Sheet", fontStyle = fonts.body2)
            }
        }
    }
}