/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.style.TextOverflow
import ai.botstacks.sdk.actions.markRead
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.IAC.colors
import ai.botstacks.sdk.ui.IAC.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.views.*
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genChat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatChat(
    chat: Chat,
    message: Message? = null,
    openProfile: (User) -> Unit,
    openInvite: (Chat) -> Unit,
    openReply: (Message) -> Unit,
    openEdit: (Chat) -> Unit,
    back: () -> Unit
) {
    val ctx = rememberCoroutineScope()
    var focusRequester = remember { FocusRequester() }
    var messageForAction by remember {
        mutableStateOf<Message?>(null)
    }
    val media = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
    val menu = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
    DisposableEffect(key1 = chat.id, effect = {
        Chat.currentlyViewed = chat.id
        chat.markRead()
        onDispose {
            if (chat.id == Chat.currentlyViewed) {
                Chat.currentlyViewed = null
            }
            chat.markRead()
        }
    })
    MediaActionSheet(
        state = media,
        chat = chat,
        dismiss = { ctx.launch { menu.hide() } },
        inReplyTo = message
    ) {
        MessageActionSheet(
            message = messageForAction,
            hide = { messageForAction = null },
            onReply = openReply
        ) {
            ChatDrawer(
                chat = chat,
                state = menu,
                hide = { ctx.launch { menu.hide() } },
                openEdit = openEdit,
                openInvite = openInvite,
                openProfile = openProfile,
                back = back
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Header(title = "", icon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Avatar(chat.displayImage, 35.0, chat != null)
                            Column {
                                Text(
                                    text = chat.displayName,
                                    iac = fonts.title2,
                                    color = colors.text,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                chat.let {
                                    ChatCount(count = it.members.size)
                                }
                            }
                        }
                    }, back = back, menu = { ctx.launch { menu.show() } })
                    MessageList(
                        chat = chat,
                        modifier = Modifier.weight(1f),
                        onPressUser = { openProfile(it) },
                        onLongPress = { messageForAction = it })
                    MessageInput(
                        chat = chat,
                        replyingTo = message,
                        focusRequester = focusRequester
                    ) {
                        ctx.launch { media.show() }
                    }
                }
            }
        }
    }
}

@IPreviews
@Composable
fun ChatChatPreview() {
    BotStacksChatContext {
        ChatChat(
            chat = genChat(),
            openProfile = {},
            openInvite = {},
            openReply = {},
            openEdit = {}
        ) {

        }
    }
}