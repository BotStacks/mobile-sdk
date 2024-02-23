/*
 * Copyright (c) 2023.
 */

@file:OptIn(ExperimentalResourceApi::class)

package ai.botstacks.sdk.ui.theme

import androidx.compose.runtime.Stable
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.utils.ui.ImageAsset
import ai.botstacks.sdk.utils.ui.ImageAssetIdentifier
import ai.botstacks.sdk.utils.ui.toImageAsset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

sealed interface EmptyScreenType {
    data object Messages : EmptyScreenType
    data object Chats : EmptyScreenType

    data object AllChannels : EmptyScreenType
}

@Stable
data class EmptyScreenConfig(
    val image: @Composable () -> ImageAsset? = { null },
    val caption: String? = null,
    val type: EmptyScreenType?
) {
    constructor(asset: ImageAssetIdentifier?, caption: String?, type: EmptyScreenType?) : this(
        image = @Composable {
            asset?.toImageAsset()
        },
        caption = caption,
        type = type,
    )
}

val EmptyScreenConfig.defaultImage: Painter?
    @Composable get() = when (type) {
        EmptyScreenType.AllChannels -> EmptyAllChannelsDefault
        EmptyScreenType.Messages -> EmptyChatDefault
        EmptyScreenType.Chats -> EmptyChatsDefault
        else -> null
    }

@Stable
data class Assets(
    val chat: ImageAssetIdentifier? = null,
    val emptyChat: EmptyScreenConfig = EmptyScreenConfig(
        caption = "No messages",
        type = EmptyScreenType.Messages
    ),
    val emptyChats: EmptyScreenConfig = EmptyScreenConfig(
        caption = "You haven't added any chats yet",
        type = EmptyScreenType.Chats
    ),
    val emptyAllChannels: EmptyScreenConfig = EmptyScreenConfig(
        caption = "No channels around here yet. Make one",
        type = EmptyScreenType.AllChannels
    ),
) {
    fun list(list: BotStacksChatStore.ChatList) = when (list) {
        BotStacksChatStore.ChatList.dms -> emptyChats
        BotStacksChatStore.ChatList.groups -> emptyChats
    }
}

internal val LocalBotStacksAssets = staticCompositionLocalOf { Assets() }

internal val EmptyChatDefault: Painter
    @Composable get() = painterResource(Res.drawable.chat_multiple_outline)
internal val EmptyChatsDefault: Painter
    @Composable get() = painterResource(Res.drawable.chat_multiple_outline)
internal val EmptyAllChannelsDefault: Painter
    @Composable get() = painterResource(Res.drawable.empty_all_channels)
