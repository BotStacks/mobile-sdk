/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.genM
import ai.botstacks.sdk.internal.utils.random
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.components.ChatMessage
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.MessageActionSheet
import ai.botstacks.sdk.ui.components.MessageList
import ai.botstacks.sdk.ui.components.rememberMessageActionSheetState
import ai.botstacks.sdk.ui.components.shapeForMessage
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
internal fun FavoritesMessagesScreen(
    back: () -> Unit,
    openReplies: (Message) -> Unit,
    openProfile: (User) -> Unit,
) {
    LaunchedEffect(Unit) {
        BotStacksChatStore.current.favorites.loadMoreIfEmpty()
    }

    val messageActionSheetState = rememberMessageActionSheetState()

    MessageActionSheet(
        state = messageActionSheetState,
    ) {
        MessageList(
            pager = BotStacksChatStore.current.favorites,
            header = {
                Header(title = "Favorite Messages", onBackClicked = back)
            },
            onPressUser = openProfile,
            onLongPress = {},
            showReplies = false,
        ) { info, message, onClick ->

            val (isPrevClose, isNextClose, arrangement) = info
            ChatMessage(
                modifier = Modifier.padding(arrangement),
                message = message,
                onPressUser = openProfile,
                shape = shapeForMessage(message.userOrNull?.isCurrent == true, isPrevClose, isNextClose),
                showTimestamp = !isNextClose,
                showAvatar = !isNextClose,
                onLongPress = { messageActionSheetState.messageForAction = message },
                openThread = { openReplies(message) },
                onClick = {
                    onClick(it)
                }
            )
        }
    }
}

@IPreviews
@Composable
private fun FavoritesViewPreview() {
    BotStacksChatStore.current.favorites.items.addAll(random(50, { genM() }))
    BotStacksThemeEngine {
        FavoritesMessagesScreen(back = {}, openReplies = {}, openProfile = {})
    }
}
