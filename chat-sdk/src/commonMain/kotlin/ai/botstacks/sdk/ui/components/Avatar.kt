/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.ui.components.OnlineStatusIndicator
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.internal.ui.components.RemoveIndicator
import ai.botstacks.sdk.internal.ui.components.SelectedBadge
import ai.botstacks.sdk.internal.ui.theme.LocalBotStacksColorPalette
import ai.botstacks.sdk.ui.theme.dayNightColor
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.state.OnlineStatus
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import ai.botstacks.`chat-sdk`.generated.resources.Res
import dev.icerock.moko.resources.compose.painterResource
import kotlin.math.roundToInt


/**
 * Size constraint system for rendering the avatar component
 */
sealed interface AvatarSize {
    val value: Dp

    /**
     * Small definition - renders at 50 dp on Android and 50px on iOS
     */
    data object Small : AvatarSize {
        override val value: Dp = 50.dp
    }

    /**
     * Large definition - renders at 100 dp on Android and 100px on iOS
     */
    data object Large : AvatarSize {
        override val value: Dp = 100.dp
    }

    /**
     * Custom definition
     */
    data class Custom(val dp: Dp) : AvatarSize {
        override val value: Dp = dp
    }
}

/**
 * Defaults for the Avatar component
 *
 * @property Size - Defaults to [AvatarSize.Small]
 * @property BackgroundColor - Defaults to #FCFCFD3 in light, #45454A in dark
 * @property ContentColor - Defaults to [ai.botstacks.sdk.ui.theme.Colors.onSurface]
 */
object AvatarDefaults {
    val Size: AvatarSize = AvatarSize.Small

    val BackgroundColor: Color
        @Composable get() = with(LocalBotStacksColorPalette.current) {
            dayNightColor(light._100, dark._500)
        }

    val ContentColor: Color
        @Composable get() = BotStacks.colorScheme.onSurface

}

/**
 * Type definition for a given [Avatar] instance
 */
sealed interface AvatarType {
    val emptyState: Painter?
        @Composable get() = null

    /**
     * Renders an Avatar for a given [User]
     * @param url The remote URL from [User.url]
     * @param status The online status for the user - if left defaulted to UNKNOWN then will not show
     * @param empty The empty state painter when fails to load
     */
    data class User(
        val url: Any?,
        val status: OnlineStatus = OnlineStatus.Unknown,
        val empty: Painter? = null,
    ) : AvatarType {
        override val emptyState: Painter
            @Composable get() = empty ?: painterResource(Res.images.user_outlined)
    }

    /**
     * Renders an Avatar for a given [Chat] channel.
     * @param urls The remote URLs for the users in the channel.
     * @param empty The empty state painter when fails to load
     */
    data class Channel(val urls: List<String?>, val empty: Painter? = null) : AvatarType {
        override val emptyState: Painter
            @Composable get() = empty ?: painterResource(Res.images.user_outlined)
    }
}

/**
 * Avatar
 *
 * Renders the display image for a user in a bordered circle at the specified size.
 *
 * This is a convenience wrapper around [User] to handle [OnlineStatus] easily.
 *
 * @param modifier the Modifier to be applied to this Avatar
 * @param size Size to display
 * @param user User for the avatar
 * @param showOnlineStatus if enabled, the online status indicator will show if not UNKNOWN.
 * @param isSelected Whether to show the selected state for this avatar (checkmark in bottom right).
 * @param isRemovable Whether to show the removable state for this avatar (x in bottom right).
 *
 * NOTE: isSelected and isRemovable supersede online status indicator.
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarDefaults.Size,
    user: User,
    showOnlineStatus: Boolean = true,
    isSelected: Boolean = false,
    isRemovable: Boolean = false,
) {
    val showIndicator = showOnlineStatus && !isSelected && !isRemovable
    val type = remember(user.avatar, user.status) {
        AvatarType.User(
            url = user.avatar,
            status = if (showIndicator) user.status else OnlineStatus.Unknown
        )
    }
    Avatar(
        modifier = modifier,
        type = type,
        size = size,
        isSelected = isSelected,
        isRemovable = isRemovable,
    )
}

/**
 * Avatar
 *
 * Renders the display image for a given URL in a bordered circle at the specified size.
 *
 * @param modifier the Modifier to be applied to this Avatar
 * @param size Size to display
 * @param status optional online status
 * @param url The URL of the image to render.
 * @param chat Whether or not this Avatar is for a chat. True for a channel, group, false for a user, DM.
 *
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarDefaults.Size,
    status: OnlineStatus = OnlineStatus.Unknown,
    url: String?,
    chat: Boolean = false,
) {
    val type = if (chat) {
        AvatarType.Channel(listOf(url))
    } else {
        AvatarType.User(url, status)
    }
    Avatar(
        type = type,
        size = size,
        modifier = modifier
    )
}

/**
 * Avatar
 *
 * Renders the display image in a bordered circle at the specified size.
 *
 * @param modifier the Modifier to be applied to this Avatar
 * @param backgroundColor Background color to render behind images (will show in empty state depending on what empty state is).
 * @param contentColor Content color for items rendered in empty state.
 * @param size Size to display
 * @param type Type definition for this Avatar.
 * @param isSelected Whether to show the selected state for this avatar (checkmark in bottom right).
 * @param isRemovable Whether to show the removable state for this avatar (x in bottom right).
 *
 */
@Composable
fun Avatar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AvatarDefaults.BackgroundColor,
    contentColor: Color = AvatarDefaults.ContentColor,
    size: AvatarSize = AvatarDefaults.Size,
    type: AvatarType,
    isSelected: Boolean = false,
    isRemovable: Boolean = false,
) {
    Box(
        modifier = modifier
            .size(size.value)
            .background(color = backgroundColor, shape = CircleShape),
    ) {
        when (type) {
            is AvatarType.Channel -> {
                val urls = type.urls
                val none = urls.all { it == null }
                if (!none) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .align(Alignment.Center),
                    ) {
                        val split = urls.take(4).chunked(2)
                        split.forEach { chunk ->
                            Column(modifier = Modifier.weight(1f)) {
                                chunk.forEach { user ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalPlatformContext.current)
                                            .data(user)
                                            .crossfade(true)
                                            .build(),
                                        onError = {
                                            Monitor.error(error = it.result.throwable, "failed to load user avatar")
                                        },
                                        fallback = type.emptyState,
                                        contentDescription = "user profile picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Image(
                        painter = type.emptyState,
                        contentDescription = "channel profile picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimens.inset)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                }
            }

            is AvatarType.User -> {
                val url = type.url
                val status = type.status
                val isVisibleStatus = remember(status) {
                    when (status) {
                        OnlineStatus.Away,
                        OnlineStatus.Offline,
                        OnlineStatus.Online -> true

                        else -> false
                    }
                }
                if (url != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(url)
                            .crossfade(true)
                            .build(),
                        fallback = type.emptyState,
                        onError = {
                            Monitor.error(error = it.result.throwable, "failed to load user avatar")
                        },
                        contentDescription = "user profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .align(Alignment.Center),
                    )
                } else {
                    Image(
                        painter = type.emptyState,
                        contentDescription = "user profile picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimens.grid.x3)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                }

                if (isVisibleStatus || isSelected || isRemovable) {
                    Layout(
                        content = {
                            if (isSelected) {
                                SelectedBadge()
                            } else if (isRemovable) {
                                RemoveIndicator()
                            } else {
                                OnlineStatusIndicator(status = status)
                            }
                        }
                    ) { measurables, constraints ->
                        val dot = measurables[0].measure(constraints)
                        layout(
                            constraints.maxWidth,
                            constraints.maxHeight
                        ) {
                            dot.placeRelative(
                                x = constraints.maxWidth - dot.width,
                                y = (constraints.maxHeight - dot.height / 1.25f).roundToInt()
                            )
                        }
                    }
                }
            }
        }
    }
}

@IPreviews
@Composable
private fun AvatarPreview() {
    Row(
        modifier = Modifier.padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val url = "https://source.unsplash.com/featured/300x200"
            Avatar(type = AvatarType.User(url))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Online))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Offline))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Away))
            Avatar(
                type = AvatarType.Channel(
                    listOf(
                        "https://source.unsplash.com/featured/300x201",

                        "https://source.unsplash.com/featured/300x202",
                        "https://source.unsplash.com/featured/300x203",
                        "https://source.unsplash.com/featured/300x205"
                    )
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val url = null
            Avatar(type = AvatarType.User(url))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Online))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Offline))
            Avatar(type = AvatarType.User(url = url, status = OnlineStatus.Away))
            Avatar(type = AvatarType.Channel(emptyList()))
        }
    }
}
