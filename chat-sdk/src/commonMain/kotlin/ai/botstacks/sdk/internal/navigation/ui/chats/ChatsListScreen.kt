/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.ui.components.AlertActionStyle
import ai.botstacks.sdk.internal.ui.components.BotStacksAlertDialog
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.ChatList
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.ui.components.rememberHeaderState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun ChatsListScreen(
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
                onBackClicked = { headerState.searchActive = false }
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

    ChatList(
        header = header,
        filter = filter@{ chat ->
            if (searchQuery.isEmpty()) return@filter true
            else chat.items.any {
                it.msg.lowercase()
                    .contains(searchQuery.lowercase()) || it.user.displayNameFb.lowercase()
                    .contains(searchQuery.lowercase())
            } || chat.name.orEmpty().lowercase().contains(searchQuery.lowercase())
        },
        onChatClicked = openChat
    )
}

