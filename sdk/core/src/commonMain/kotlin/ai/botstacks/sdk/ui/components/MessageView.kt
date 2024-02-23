/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.actions.react
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.state.location
import ai.botstacks.sdk.type.AttachmentType
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacks.shapes
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.Pressable
import ai.botstacks.sdk.utils.Fn
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.format
import ai.botstacks.sdk.utils.genChatextMessage
import ai.botstacks.sdk.utils.genCurrentUser
import ai.botstacks.sdk.utils.genU
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import botstacks.sdk.core.generated.resources.Res
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    message: Message,
    isGroup: Boolean,
    shape: CornerBasedShape = shapes.medium,
    showAvatar: Boolean = false,
    showTimestamp: Boolean = true,
    onPressUser: (User) -> Unit,
    onLongPress: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    if (message.user.blocked) {
        return
    }
    val current = message.user.isCurrent
    val align = if (current) Alignment.End else Alignment.Start

    message.attachments.onEachIndexed { index, attachment ->

        val showAvatarForThis = showAvatar &&
                index == message.attachments.lastIndex &&
                message.markdown.isEmpty()

        val showTimestampForThis = showTimestamp &&
                index == message.attachments.lastIndex &&
                message.markdown.isEmpty()

        MessageView(
            modifier = modifier,
            avatar = message.user.avatar,
            username = message.user.displayNameFb,
            content = null,
            attachment = attachment,
            date = message.createdAt,
            isCurrentUser = current,
            isGroup = isGroup,
            shape = shape,
            alignment = align,
            showAvatar = showAvatarForThis,
            showTimestamp = showTimestampForThis,
            isSending = message.isSending,
            hasError = message.failed,
            onPressUser = { onPressUser(message.user) },
            onLongPress = onLongPress,
            onClick = onClick
        )
    }

    if (message.markdown.isNotEmpty()) {
        MessageView(
            modifier = modifier,
            avatar = message.user.avatar,
            username = message.user.displayNameFb,
            content = message.markdown,
            attachment = null,
            date = message.createdAt,
            isCurrentUser = current,
            isGroup = isGroup,
            shape = shape,
            alignment = align,
            showAvatar = message.attachments.isEmpty() && showAvatar,
            showTimestamp = message.attachments.isEmpty() && showTimestamp,
            isSending = message.isSending,
            hasError = message.failed,
            onPressUser = { onPressUser(message.user) },
            onLongPress = onLongPress,
            onClick = onClick
        )
    }
}

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    avatar: String?,
    username: String,
    content: String? = null,
    attachment: FMessage.Attachment? = null,
    date: Instant,
    isCurrentUser: Boolean,
    isGroup: Boolean,
    shape: CornerBasedShape = shapes.medium,
    alignment: Alignment.Horizontal,
    showAvatar: Boolean = false,
    showTimestamp: Boolean = true,
    isSending: Boolean = false,
    hasError: Boolean = false,
    onPressUser: () -> Unit,
    onLongPress: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val maxWidth = maxWidth
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.grid.x2, alignment),
                verticalAlignment = Alignment.Bottom,
            ) {
                if (alignment == Alignment.Start && isGroup) {
                    if (showAvatar) {
                        Avvy(avatar) { onPressUser() }
                    } else {
                        Spacer(Modifier.requiredWidth(AvatarSize.Small.value))
                    }
                }

                when (attachment?.type) {
                    AttachmentType.image -> MessageImageContent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = maxWidth * 0.67f),
                        isCurrentUser = isCurrentUser,
                        image = attachment,
                        username = username,
                        shape = shape,
                        showOwner = isGroup && !isCurrentUser && showTimestamp,
                        onClick = onClick,
                        onLongClick = onLongPress,
                    )

                    AttachmentType.location -> MessageMapContent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = maxWidth * 0.67f),
                        location = attachment.location()!!,
                        isCurrentUser = isCurrentUser,
                        username = username,
                        avatar = avatar,
                        shape = shape,
                        showOwner = isGroup && !isCurrentUser && showTimestamp,
                        onClick = onClick,
                        onLongClick = onLongPress,
                    )
                    else -> Unit
                }

                if (content != null) {
                    MessageTextContent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = maxWidth * 0.56f),
                        shape = shape,
                        content = content,
                        showOwner = isGroup && !isCurrentUser && showTimestamp,
                        isCurrentUser = isCurrentUser,
                        username = username,
                        onClick = onClick,
                        onLongClick = onLongPress,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimens.grid.x2, alignment = alignment),
            ) {
                Spacer(Modifier.requiredWidth(AvatarSize.Small.value))
                if (showTimestamp) {
                    when {
                        isSending -> {
                            Text(
                                text = "Sending...",
                                fontStyle = fonts.caption2,
                                color = colorScheme.primary
                            )
                        }
                        hasError -> {
                            Text(
                                text = "Failed to send.",
                                fontStyle = fonts.caption2,
                                color = colorScheme.error
                            )
                        }
                        else -> {
                            Text(
                                text = date.format("h:mm a"),
                                fontStyle = fonts.caption2,
                                color = colorScheme.caption
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
fun MessageViewPreview() {
    BotStacksChatContext {
        Column {
            MessageView(
                message = genChatextMessage(genU()),
                isGroup = false,
                onPressUser = {},
                onLongPress = {})
            MessageView(
                message = genChatextMessage(genCurrentUser()),
                isGroup = false,
                onPressUser = {},
                onLongPress = {})
        }
    }
}

@Composable
fun ReplyCount(count: Int) {
    if (count > 0) {
        Text(
            text = "$count replies",
            fontStyle = fonts.body1,
            color = colorScheme.primary,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Favorite(favorite: Boolean) {
    if (favorite) {
        Box(modifier = Modifier.size(35.dp), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(Res.drawable.star),
                contentDescription = "favorite",
                tint = colorScheme.primary,
                modifier = Modifier.size(20),
            )
        }
    }
}

@Composable
fun Spinner(loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(color = colorScheme.primary, modifier = Modifier.size(20))
    }
}

@Composable
fun Avvy(url: String?, onClick: Fn) {
    Pressable(onClick = onClick) {
        println("User avatar $url")
        Avatar(url = url)
    }
}

@Composable
fun Reactions(msg: Message, modifier: Modifier = Modifier) {
    for (reactions in msg.reactions.chunked(5)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
            for (reaction in reactions) {
                ClickableText(
                    text = "${reaction.first} ${
                        reaction.second
                            .size
                    }",
                    onClick = { msg.react(reaction.first) },
                    iac = fonts.body1,
                    color = colorScheme.onBackground,
                    modifier = Modifier
                        .radius(36.dp)
                        .background(Color.Transparent)
                        .border(
                            2.dp,
                            if (msg.currentReaction == reaction.first) colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(36.dp)
                        )
                        .padding(dimens.grid.x2)
                )
            }
        }
    }
}