/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.ImageRenderer
import ai.botstacks.sdk.ui.components.internal.MarkdownView
import ai.botstacks.sdk.ui.components.internal.location.MapPin
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.ift
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageTextContent(
    modifier: Modifier = Modifier,
    isCurrentUser: Boolean,
    username: String,
    content: String,
    shape: CornerBasedShape = BotStacks.shapes.medium,
    showOwner: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: () -> Unit = { }
) {
    Column(
        modifier = modifier
            .background(
                color = ift(
                    isCurrentUser,
                    colorScheme.primary,
                    colorScheme.message
                ),
                shape = shape
            ).clip(shape)
            .combinedClickable(
                onClick = { onClick?.invoke() },
                onLongClick = onLongClick,
            ).padding(horizontal = dimens.grid.x2, vertical = dimens.grid.x1),
    ) {
        if (showOwner) {
            Text(
                text = username,
                fontStyle = BotStacks.fonts.label2,
                color = colorScheme.primary
            )
        }
        if (content.isNotEmpty()) {
            MarkdownView(
                content = content,
                isCurrentUser = isCurrentUser,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageImageContent(
    username: String,
    image: FMessage.Attachment,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = BotStacks.shapes.medium,
    showOwner: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: () -> Unit = { }
) {
    Column(
        modifier = modifier,
    ) {
        if (showOwner) {
            Text(
                text = username,
                fontStyle = BotStacks.fonts.label2,
                color = colorScheme.primary
            )
        }

        ImageRenderer(
            url = image.url,
            contentDescription = "shared image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .heightIn(min = dimens.imagePreviewSize.height.dp)
                .fillMaxWidth()
                .background(
                    color = ift(
                        isCurrentUser,
                        colorScheme.primary,
                        colorScheme.message
                    ),
                    shape = shape
                ).clip(shape),
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageMapContent(
    username: String,
    avatar: String?,
    location: Location,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = BotStacks.shapes.medium,
    showOwner: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: () -> Unit = { }
) {
    Column(
        modifier = modifier,
    ) {
        if (showOwner) {
            Text(
                text = username,
                fontStyle = BotStacks.fonts.label2,
                color = colorScheme.primary
            )
        }

        if (BotStacksChat.shared.hasMapsSupport) {
            MapPin(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.imagePreviewSize.height.dp)
                    .background(
                        color = ift(
                            isCurrentUser,
                            colorScheme.primary,
                            colorScheme.message
                        ),
                        shape = shape
                    ).clip(shape)
                    .combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = onLongClick,
                    ),
                location = location,
                userAvatar = avatar
            )
        } else {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(dimens.imagePreviewSize.height.dp)
                .background(
                    color = ift(
                        isCurrentUser,
                        colorScheme.primary,
                        colorScheme.message
                    ),
                    shape = shape
                ).clip(shape)
                .combinedClickable(
                    onClick = { onClick?.invoke() },
                    onLongClick = onLongClick,
                ),)
        }
    }
}

//if (!message.attachments.isEmpty()) {
//    FlowRow(
//        horizontalArrangement = Arrangement.spacedBy(
//            4.dp,
//            alignment = if (message.user.isCurrent) Alignment.End else Alignment.Start
//        ),
//    ) {
//        for (attachment in message.attachments) {
//            when (attachment.type) {
//                AttachmentType.image ->
//
//                AttachmentType.video -> VideoPlayer(
//                    url = attachment.url,
//                    modifier = Modifier
//                        .width(dimens.videoPreviewSize.width.dp)
//                        .height(dimens.videoPreviewSize.height.dp)
//                        .clip(RoundedCornerShape(15.dp))
//                )
//
//                AttachmentType.audio -> AudioPlayer(url = attachment.url)
//
//                AttachmentType.file -> Image(
//                    painter = painterResource(Res.drawable.file_arrow_down_fill),
//                    contentDescription = "File",
//                    colorFilter = ColorFilter.tint(
//                        ift(
//                            message.user.isCurrent,
//                            colorScheme.onPrimary,
//                            colorScheme.onMessage
//                        )
//                    ),
//                    modifier = Modifier.size(64)
//                )
//
//                AttachmentType.location, AttachmentType.vcard -> MarkdownView(
//                    content = attachment.location()?.markdown
//                        ?: attachment.vcard()?.markdown()
//                        ?: "No content",
//                    isCurrentUser = message.user.isCurrent,
//                )
//
//                else -> {
//                    MarkdownView(
//                        content = message.markdown,
//                        isCurrentUser = message.user.isCurrent,
//                    )
//                }
//            }
//        }
//    }
//}

@IPreviews
@Composable
fun MessageContentPreview() {
    BotStacksChatContext {
        Column {
//            MessageImageContent(message = genImageMessage())
//            MessageContent(message = genFileMessage())
//            MessageTextContent(message = genChatextMessage())
        }
    }
}