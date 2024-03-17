/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.Monitor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.internal.navigation.ui.NotFound
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.Spinner


@Composable
internal fun ConversationRouter(
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
            Monitor.debug("gid trigger")
            try {
                chat = API.getChat(gid)
            } catch (err: Exception) {
                Monitor.error(err)
                notFound = true
            } catch (err: Error) {
                Monitor.error(err)
                notFound = true
            }
        }
    }

    LaunchedEffect(uid) {
        if (uid != null && chat == null) {
            Monitor.debug("uid trigger")
            try {
                chat = API.dm(uid)
            } catch (err: Exception) {
                Monitor.error(err)
                notFound = true
            } catch (err: Error) {
                Monitor.error(err)
                notFound = true
            }
        }
    }

    LaunchedEffect(mid) {
        if (mid != null && chat == null) {
            Monitor.debug("mid trigger")
            try {
                val m = Message.get(mid) ?: API.getMessage(mid)
                m?.let {
                    chat = Chat.get(it.chatID) ?: API.getChat(it.chatID)
                }
            } catch (err: Exception) {
                Monitor.error(err)
                notFound = true
            } catch (err: Error) {
                Monitor.error(err)
                notFound = true
            }
        }
    }

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