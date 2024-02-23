/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components.internal

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
import ai.botstacks.sdk.ui.components.Text
import androidx.compose.ui.graphics.painter.Painter
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ActionItem(text: String, icon: DrawableResource, action: () -> Unit) {
    ActionItem(text, painterResource(icon), action)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ActionItem(text: String, icon: Painter, action: () -> Unit) {
    Column {
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
                    tint = BotStacks.colorScheme.onBackground,
                    modifier = Modifier.size(25.dp)
                )
            },
            modifier = Modifier.clickable {
                action()
            }
        )
    }
}

object ActionItemDefaults {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun mediaItems(onItemSelected: (Media) -> Unit): List<@Composable () -> Unit> = Media.supportedMediaTypes
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
                            icon = painterResource(Res.drawable.image_square),
                            text = "Upload Photo",
                            action = action,
                        )
                    }

                    Media.pickVideo -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.file_video),
                            text = "Upload Video",
                            action = action,
                        )
                    }

                    Media.recordPhoto -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.camera),
                            text = "Take Photo",
                            action = action,
                        )
                    }

                    Media.recordVideo -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.video_camera),
                            text = "Video Camera",
                            action = action,
                        )
                    }

                    Media.gif -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.gif),
                            text = "Send a GIF",
                            action = action,
                        )
                    }

                    Media.file -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.file_arrow_down_fill),
                            text = "Send a file",
                            action = action,
                        )
                    }

                    Media.contact -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.address_book),
                            text = "Share Contact",
                            action = action,
                        )
                    }

                    Media.location -> {
                        ActionItem(
                            icon = painterResource(Res.drawable.map_pin),
                            text = "Send Location",
                            action = action,
                        )
                    }
                }
            }
        }
}