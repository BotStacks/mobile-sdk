/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui.chats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ai.botstacks.sdk.API
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.navigation.ui.NotFound
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.Spinner
import ai.botstacks.sdk.utils.Monitoring
import co.touchlab.kermit.Logger


@Composable
fun ConversationRouter(
    gid: String? = null,
    uid: String? = null,
    mid: String? = null,
    openProfile: (User) -> Unit,
    openInvite: (Chat) -> Unit,
    openEditChat: (Chat) -> Unit,
    openReply: (Message) -> Unit,
    back: () -> Unit
) {
    var chat by remember {
        mutableStateOf(gid?.let { Chat.get(it) } ?: uid?.let { Chat.getByUser(it) }
        ?: mid?.let { Message.get(it)?.chat })
    }
    var notFound by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(gid) {
        if (gid != null && chat == null) {
            Logger.d("gid trigger")
            try {
                chat = API.getChat(gid)
            } catch (err: Exception) {
                Monitoring.error(err)
                notFound = true
            } catch (err: Error) {
                Monitoring.error(err)
                notFound = true
            }
        }
    }

    LaunchedEffect(uid) {
        if (uid != null && chat == null) {
            Logger.d("uid trigger")
            try {
                chat = API.dm(uid)
            } catch (err: Exception) {
                Monitoring.error(err)
                notFound = true
            } catch (err: Error) {
                Monitoring.error(err)
                notFound = true
            }
        }
    }

    LaunchedEffect(mid) {
        if (mid != null && chat == null) {
            Logger.d("mid trigger")
            try {
                val m = Message.get(mid) ?: API.getMessage(mid)
                m?.let {
                    chat = Chat.get(it.chatID) ?: API.getChat(it.chatID)
                }
            } catch (err: Exception) {
                Monitoring.error(err)
                notFound = true
            } catch (err: Error) {
                Monitoring.error(err)
                notFound = true
            }
        }
    }

    Logger.d("chatId=${chat?.id}")
    chat.let {
        if (it != null) {
            ConversationScreen(
                chat = it,
                openProfile = openProfile,
                openInvite = openInvite,
                openReply = openReply,
                openEdit = { openEditChat(it) },
                back = back
            )
        } else {
            if (notFound) NotFound(back = back) else Column {
                Header(title = "Chat")
                Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                    Spinner()
                }
            }
        }
    }
}