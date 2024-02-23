/*
 * Copyright (c) 2023.
 */

@file:OptIn(ExperimentalFoundationApi::class)

package ai.botstacks.sdk.navigation.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.ChatRow
import ai.botstacks.sdk.ui.components.EmptyListView
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.ui.components.IACList
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.ui.components.internal.AlertActionStyle
import ai.botstacks.sdk.ui.components.internal.BotStacksAlertDialog
import ai.botstacks.sdk.ui.components.rememberHeaderState
import ai.botstacks.sdk.utils.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi

@Composable
fun ChatsListScreen(
    scrollToTop: Int = 0,
    openChat: (Chat) -> Unit,
    onCreateChannel: () -> Unit,
    editProfile: () -> Unit,
    openFavorites: () -> Unit,
    onConfirmedLogout: () -> Unit,
) {
    var logoutRequested by remember {
        mutableStateOf(false)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    val headerState = rememberHeaderState(
        isSearchVisible = true,
    )

    val header = @Composable {
        Column {
            Header(
                icon = { HeaderDefaults.Logo() },
                state = headerState,
                onCompose = onCreateChannel,
                onBackClick = { headerState.searchActive = false }
                    .takeIf { headerState.searchActive },
                menu = {
                    label(onClick = openFavorites) {
                        Text(text = "Favorite Messages", fontStyle = BotStacks.fonts.button2)
                    }
                    label(onClick = editProfile) {
                        Text(text = "Edit Profile", fontStyle = BotStacks.fonts.button2)
                    }
                    label(onClick = { logoutRequested = true }) {
                        Text(
                            text = "Logout",
                            fontStyle = BotStacks.fonts.button2,
                            color = BotStacks.colorScheme.error
                        )
                    }
                }
            )
        }
    }

    AnimatedVisibility(logoutRequested) {
        BotStacksAlertDialog(
            title = "Log out",
            message = "Are you sure you want to log out?",
            buttons = {
                button(
                    onClick = { logoutRequested = false },
                    title = "Cancel",
                    style = AlertActionStyle.Cancel
                )
                button(
                    onClick = {
                        logoutRequested = false
                        onConfirmedLogout()
                    },
                    title = "Log out",
                    style = AlertActionStyle.Destructive
                )
            },
            onDismissRequest = { logoutRequested = false }
        )
    }


    LaunchedEffect(headerState.searchQuery) {
        if (searchQuery != headerState.searchQuery.text) {
            searchQuery = headerState.searchQuery.text
        }
    }


    val items = BotStacksChatStore.current.chats
        .filter { chat ->
            if (searchQuery.isEmpty()) return@filter true
            else chat.items.any {
                it.msg.lowercase()
                    .contains(searchQuery.lowercase()) || it.user.displayNameFb.lowercase()
                    .contains(searchQuery.lowercase())
            } || chat.name.orEmpty().lowercase().contains(searchQuery.lowercase())
        }.sortedByDescending { it.latest?.createdAt }.distinctBy { it.id }

    IACList(
        items = items,
        header = header,
        empty = @Composable {
            EmptyListView(config = BotStacks.assets.emptyChats)
        },
        scrollToTop = scrollToTop.toString(),
    ) {
        ChatRow(chat = it, onClick = { openChat(it) })
    }
}

@IPreviews
@Composable
fun EmptyUserChatsViewPreview() {
    BotStacksChatContext {
        ChatsListScreen(
            openChat = {},
            onCreateChannel = { /*TODO*/ },
            editProfile = {},
            openFavorites = {},
            onConfirmedLogout = {}
        )
    }
}

@IPreviews
@Composable
fun ChatsViewPreview() {
//    Chats.current.users.items.addAll(random(10, { genUserChat() }))
//    Chats.current.chats.items.addAll(random(10, { genChatChat() }))
//    Chats.current.messages.items.addAll(random(10, { genRepliesMessage() }))
    BotStacksChatContext {
        ChatsListScreen(
            openChat = {},
            onCreateChannel = { /*TODO*/ },
            editProfile = {},
            openFavorites = {},
            onConfirmedLogout = {}
        )
    }
}

