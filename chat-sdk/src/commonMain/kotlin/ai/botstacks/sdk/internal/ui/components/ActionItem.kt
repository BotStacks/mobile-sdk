/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import ai.botstacks.`chat-sdk`.generated.resources.Res
import ai.botstacks.sdk.BotStacksChat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.Media
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource


@Composable
internal fun ActionItem(
    text: String,
    iconTint: Color = BotStacks.colorScheme.primary,
    icon: ImageResource,
    action: () -> Unit
) {
    ActionItem(
        text = text,
        icon = painterResource(icon),
        iconTint = iconTint,
        action = action
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ActionItem(
    text: String,
    icon: Painter,
    iconTint: Color = BotStacks.colorScheme.primary,
    action: () -> Unit
) {
    ListItem(
        text = {
            Text(
                text = text,
                fontStyle = BotStacks.fonts.body2,
                color = BotStacks.colorScheme.onBackground
            )
        },
        icon = {
            Icon(
                painter = icon,
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(25.dp)
            )
        },
        modifier = Modifier.clickable {
            action()
        }
    )
}

internal object ActionItemDefaults {
    @Composable
    fun mediaItems(onItemSelected: (Media) -> Unit): List<@Composable () -> Unit> =
        Media.supportedMediaTypes
            .filter {
                if (it == Media.location && !BotStacksChat.shared.hasLocationSupport) {
                    return@filter false
                }

                if (it == Media.gif && !BotStacksChat.shared.hasGiphySupport) {
                    return@filter false
                }

                return@filter true
            }
            .map {
                val action = { onItemSelected(it) }
                {
                    when (it) {
                        Media.pickPhoto -> {
                            ActionItem(
                                icon = painterResource(Res.images.image_square),
                                text = "Upload Photo",
                                action = action,
                            )
                        }

                        Media.pickVideo -> {
                            ActionItem(
                                icon = painterResource(Res.images.file_video),
                                text = "Upload Video",
                                action = action,
                            )
                        }

                        Media.recordPhoto -> {
                            ActionItem(
                                icon = painterResource(Res.images.camera),
                                text = "Take Photo",
                                action = action,
                            )
                        }

                        Media.recordVideo -> {
                            ActionItem(
                                icon = painterResource(Res.images.video_camera),
                                text = "Video Camera",
                                action = action,
                            )
                        }

                        Media.gif -> {
                            ActionItem(
                                icon = painterResource(Res.images.gif),
                                text = "Send a GIF",
                                action = action,
                            )
                        }

                        Media.file -> {
                            ActionItem(
                                icon = painterResource(Res.images.file_upload),
                                text = "Send a file",
                                action = action,
                            )
                        }

                        Media.contact -> {
                            ActionItem(
                                icon = painterResource(Res.images.address_book),
                                text = "Share Contact",
                                action = action,
                            )
                        }

                        Media.location -> {
                            ActionItem(
                                icon = painterResource(Res.images.map_pin),
                                text = "Send Location",
                                action = action,
                            )
                        }
                    }
                }
            }
}