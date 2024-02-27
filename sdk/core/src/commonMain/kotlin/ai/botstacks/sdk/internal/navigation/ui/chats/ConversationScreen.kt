/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.actions.markRead
import ai.botstacks.sdk.internal.ui.components.ChatCount
import ai.botstacks.sdk.internal.ui.components.MediaActionSheet
import ai.botstacks.sdk.internal.ui.components.MessageActionSheet
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
import ai.botstacks.sdk.ui.components.MessageList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ConversationScreen(
    chat: Chat,
    message: Message? = null,
    openProfile: (User) -> Unit,
    openInvite: (Chat) -> Unit,
    openReply: (Message) -> Unit,
    openEdit: () -> Unit,
    back: () -> Unit
) {
    val composeScope = rememberCoroutineScope()
    var messageForAction by remember {
        mutableStateOf<Message?>(null)
    }
    val media = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )

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

    MediaActionSheet(
        state = media,
        chat = chat,
        inReplyTo = message
    ) {
        MessageActionSheet(
            message = messageForAction,
            hide = { messageForAction = null },
            onReply = openReply
        ) {

            Column(modifier = Modifier.fillMaxSize()) {
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
                    onBackClick = back,
                    endAction = { HeaderDefaults.MenuAction { openEdit() } }
                )
                MessageList(
                    chat = chat,
                    modifier = Modifier.weight(1f),
                    onPressUser = { openProfile(it) },
                    onLongPress = { messageForAction = it },
                )
                ChatInput(
                    modifier = Modifier.padding(dimens.grid.x4)
                        .navigationBarsPadding(),
                    chat = chat,
                    replyingTo = message,
                    onMedia = { composeScope.launch { media.show() } }
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