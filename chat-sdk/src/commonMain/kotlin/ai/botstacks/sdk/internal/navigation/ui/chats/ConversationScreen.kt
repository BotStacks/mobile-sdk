/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.actions.markRead
import ai.botstacks.sdk.internal.ui.components.ChatCount
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.genChat
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.components.Avatar
import ai.botstacks.sdk.ui.components.AvatarType
import ai.botstacks.sdk.ui.components.ChatInput
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.ui.components.MediaActionSheet
import ai.botstacks.sdk.ui.components.MessageActionSheet
import ai.botstacks.sdk.ui.components.MessageList
import ai.botstacks.sdk.ui.components.rememberMediaActionSheetState
import ai.botstacks.sdk.ui.components.rememberMessageActionSheetState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

@Composable
internal fun ConversationScreen(
    chat: Chat,
    openProfile: (User) -> Unit,
    openInvite: (Chat) -> Unit,
    openReply: (Message) -> Unit,
    openEdit: () -> Unit,
    back: () -> Unit
) {
    val composeScope = rememberCoroutineScope()

    DisposableEffect(chat.id) {
        Chat.currentlyViewed = chat.id
        chat.markRead()
        onDispose {
            if (chat.id == Chat.currentlyViewed) {
                Chat.currentlyViewed = null
            }
            chat.markRead()
        }
    }

    val mediaSheetState = rememberMediaActionSheetState(chat = chat)
    val messageActionSheetState = rememberMessageActionSheetState()

    MediaActionSheet(state = mediaSheetState,) {
        MessageActionSheet(
            state = messageActionSheetState,
            openThread = openReply
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                MessageList(
                    chat = chat,
                    header = {
                        Header(
                            icon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(dimens.grid.x4)
                                ) {
                                    Avatar(
                                        type = AvatarType.Channel(listOf(chat.displayImage)),
                                    )
                                    Column {
                                        Text(
                                            text = chat.displayName,
                                            fontStyle = fonts.h2,
                                            color = colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        ChatCount(count = chat.members.size)
                                    }
                                }
                            },
                            onBackClicked = back,
                            endAction = { HeaderDefaults.MenuAction { openEdit() } }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onPressUser = { openProfile(it) },
                    onLongPress = { messageActionSheetState.messageForAction = it },
                )
                ChatInput(
                    modifier = Modifier.padding(dimens.grid.x4)
                        .navigationBarsPadding(),
                    chat = chat,
                    onMedia = { composeScope.launch { mediaSheetState.show() } }
                )
            }
        }
    }
}

@IPreviews
@Composable
private fun ChatChatPreview() {
    BotStacksThemeEngine {
        ConversationScreen(
            chat = genChat(),
            openProfile = {},
            openInvite = {},
            openReply = {},
            openEdit = {}
        ) {

        }
    }
}