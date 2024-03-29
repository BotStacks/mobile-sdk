/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.theme

import ai.botstacks.`chat-sdk`.generated.resources.Res
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.compose.painterResource

/**
 * Type determination for empty states for certain components
 */
sealed interface EmptyScreenType {
    /**
     * Empty state for within a chat (no messages)
     */
    data object Messages : EmptyScreenType

    /**
     * Empty state for chat list (no chats)
     */
    data object Chats : EmptyScreenType
}

/**
 * Configuration for an empty state
 *
 * @param image Image to be display above [caption], if provided.
 * @param caption Text to be displayed below [image], if provided.
 * @param type The [EmptyScreenType] for this configuration.
 */
sealed class EmptyScreenConfig(
    open val image: @Composable () -> ImageAsset? = { null },
    open val caption: String? = null,
    open val type: EmptyScreenType
) {
    data class Chats(
        override val image: @Composable () -> ImageAsset? = { null },
        override val caption: String? = null,
    ) : EmptyScreenConfig(
        image = image,
        caption = caption,
        type = EmptyScreenType.Chats,
    )

    data class Messages(
        override val image: @Composable () -> ImageAsset? = { null },
        override val caption: String? = null
    ) : EmptyScreenConfig(
        image = image,
        caption = caption,
        type = EmptyScreenType.Messages,
    )
}

internal val EmptyScreenConfig.defaultImage: Painter
    @Composable get() = when (type) {
        EmptyScreenType.Messages -> EmptyChatDefault
        EmptyScreenType.Chats -> EmptyChatsDefault
    }

/**
 * Assets to be utilized and customized for on-brand experience within BotStacks.
 *
 * @param logo An optional logo, will default to the BotStacks Logo if not provided.
 * @param emptyChat Empty state configuration for [EmptyScreenType.Messages]
 * @param emptyChats Empty state configuration for [EmptyScreenType.Chats]
 */
@Stable
data class Assets(
    val logo: ImageAssetIdentifier? = null,
    val emptyChat: EmptyScreenConfig.Messages = EmptyScreenConfig.Messages(
        caption = "No messages",
    ),
    val emptyChats: EmptyScreenConfig.Chats = EmptyScreenConfig.Chats(
        caption = "You haven't added any chats yet",
    ),
) {
    /**
     * Compose Multiplatform [Painter] override for [logo].
     *
     * This allows use of Compose Multiplatform Resources or Moko Resources.
     */
    var logoPainter: Painter? = null
}

val LocalBotStacksAssets = staticCompositionLocalOf { Assets() }

@Composable
internal fun Assets.logoPainter(): Painter? {
    return logoPainter ?: logo?.let { painterImageAsset(it) }
}

internal val EmptyChatDefault: Painter
    @Composable get() = painterResource(Res.images.chat_multiple_outline)
internal val EmptyChatsDefault: Painter
    @Composable get() = painterResource(Res.images.chat_multiple_outline)
internal val EmptyAllChannelsDefault: Painter
    @Composable get() = painterResource(Res.images.empty_all_channels)
