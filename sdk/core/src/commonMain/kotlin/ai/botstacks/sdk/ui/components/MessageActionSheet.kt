/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import ai.botstacks.sdk.actions.toggleFavorite
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.ActionItem
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.annotated
import ai.botstacks.sdk.utils.genChatextMessage
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
fun MessageActionSheet(
    message: Message?,
    onReply: (Message) -> Unit,
    hide: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = {
            if (it == ModalBottomSheetValue.Hidden) {
                hide()
            }
            true
        }
    )
    LaunchedEffect(message) {
        if (message != null) {
            state.show()
        } else {
            state.hide()
        }
    }

    val annotatedString = (message?.text ?: "").annotated()

    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = state,
        sheetBackgroundColor = colorScheme.background,
        sheetContentColor = colorScheme.onBackground,
        scrimColor = colorScheme.scrim,
        sheetContent = {
            Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                Space(8f)
                // TODO: add back once emoji keyboard is KMP
//                EmojiBar(
//                    current = message?.currentReaction,
//                    onEmoji = {
//                        message?.react(it)
//                        hide()
//                    }
//                )
                ActionItem(
                    text = "Reply in Chat",
                    icon = Res.drawable.chat_dots,
                ) {
                    message?.let(onReply)
                    hide()
                }

                ActionItem(
                    text = if (message?.favorite == true) "Remove from Favorites" else "Save to Favorites",
                    icon = Res.drawable.star_fill,
                )
                {
                    message?.toggleFavorite()
                    hide()
                }

                ActionItem(
                    text = "Copy message text",
                    icon = Res.drawable.copy,
                ) {
                    clipboardManager.setText(annotatedString)
                    hide()
                }
            }
        },
        content = content
    )
}

@IPreviews
@Composable
fun MessageActionSheetPreview() {
    BotStacksChatContext {
        var message by remember {
            mutableStateOf<Message?>(genChatextMessage())
        }
        MessageActionSheet(message = message, hide = {}, onReply = {}) {
            Button(onClick = { message = genChatextMessage() }) {
                Text(text = "Open Sheet", fontStyle = fonts.body2)
            }
        }
    }
}