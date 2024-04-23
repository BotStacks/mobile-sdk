/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.navigation.ui.chats

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.ChatMessage
import ai.botstacks.sdk.internal.ui.components.PagerList
import ai.botstacks.sdk.internal.ui.components.ScrollStartPosition
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.genM
import ai.botstacks.sdk.internal.utils.random

@Composable
internal fun FavoritesMessagesScreen(
    back: () -> Unit,
    openReplies: (Message) -> Unit,
    openProfile: (User) -> Unit,
) {
    LaunchedEffect(key1 = true, block = {
        BotStacksChatStore.current.favorites.loadMoreIfEmpty()
    })
    Column {
        Header(title = "Favorite Messages", onBackClicked = back)
        PagerList(
            pager = BotStacksChatStore.current.favorites,
            scrollAtStart = ScrollStartPosition.None
        ) { message ->
            ChatMessage(
                message = message,
                onPressUser = openProfile,
                onLongPress = {}
            ) { openReplies(message) }
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
