package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.actions.react
import ai.botstacks.sdk.internal.ui.components.ClickableText
import ai.botstacks.sdk.internal.ui.components.MessageImageContent
import ai.botstacks.sdk.internal.ui.components.MessageMapContent
import ai.botstacks.sdk.internal.ui.components.MessageTextContent
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacks.shapes
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.ui.components.Pressable
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.internal.ui.components.radius
import ai.botstacks.sdk.internal.ui.components.size
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.format
import ai.botstacks.sdk.internal.utils.genChatextMessage
import ai.botstacks.sdk.internal.utils.genCurrentUser
import ai.botstacks.sdk.internal.utils.genU
import ai.botstacks.sdk.internal.utils.location
import ai.botstacks.sdk.state.AttachmentType
import ai.botstacks.sdk.state.MessageAttachment
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
import ai.botstacks.`chat-sdk`.generated.resources.Res
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.datetime.Instant

/**
 * ChatMessage
 *
 * Renders the contents of a given [Message] from a [ai.botstacks.sdk.state.Chat].
 * This is used by [MessageList] to form the contents of a conversational chat, by properly aligning
 * messages to left or right depending on sender (left aligned for incoming and right aligned for outgoing).
 *
 *
 * @param modifier The modifier to apply to this message.
 * @param message The message to display.
 * @param shape corner-based shaped to render the "bubble" in. This defaults to [ai.botstacks.sdk.ui.theme.ShapeDefinitions.medium].
 * @param showAvatar Whether to show the associated user's avatar along with this message.
 * @param showTimestamp Whether to show the timestamp this message was sent or received.
 * @param onPressUser callback for when a user's avatar (when visible) is clicked.
 * @param onLongPress callback for when a message "bubble" is clicked.
 * @param onClick callback for when an attachment is clicked. This is utlized by [MessageList] to show
 * images for full screen viewing.
 *
 */
@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: Message,
    shape: CornerBasedShape = shapes.medium,
    showAvatar: Boolean = false,
    showTimestamp: Boolean = true,
    onPressUser: (User) -> Unit,
    onLongPress: () -> Unit,
    onClick: ((MessageAttachment?) -> Unit)? = null
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

        ChatMessage(
            modifier = modifier,
            avatar = message.user.avatar,
            username = message.user.displayNameFb,
            content = null,
            attachment = attachment,
            date = message.createdAt,
            isCurrentUser = current,
            isGroup = message.isGroup,
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
        ChatMessage(
            modifier = modifier,
            avatar = message.user.avatar,
            username = message.user.displayNameFb,
            content = message.markdown,
            attachment = null,
            date = message.createdAt,
            isCurrentUser = current,
            isGroup = message.isGroup,
            shape = shape,
            alignment = align,
            showAvatar = message.attachments.isEmpty() && showAvatar,
            showTimestamp = message.attachments.isEmpty() && showTimestamp,
            isSending = message.isSending,
            hasError = message.failed,
            onPressUser = { onPressUser(message.user) },
            onLongPress = onLongPress,
            onClick = null
        )
    }
}

@Composable
private fun ChatMessage(
    modifier: Modifier = Modifier,
    avatar: String?,
    username: String,
    content: String? = null,
    attachment: MessageAttachment? = null,
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
    onClick: ((MessageAttachment?) -> Unit)? = null
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
                    AttachmentType.Image -> MessageImageContent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = maxWidth * 0.67f),
                        isCurrentUser = isCurrentUser,
                        image = attachment,
                        username = username,
                        shape = shape,
                        showOwner = isGroup && !isCurrentUser && showTimestamp,
                        onClick = { onClick?.invoke(attachment) },
                        onLongClick = onLongPress,
                    )

                    AttachmentType.Location -> MessageMapContent(
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = maxWidth * 0.67f),
                        location = attachment.location()!!,
                        isCurrentUser = isCurrentUser,
                        username = username,
                        avatar = avatar,
                        shape = shape,
                        showOwner = isGroup && !isCurrentUser && showTimestamp,
                        onClick = { onClick?.invoke(attachment) },
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
                        onClick = { onClick?.invoke(null) },
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
private fun MessageViewPreview() {
    BotStacksThemeEngine {
        Column {
            ChatMessage(
                message = genChatextMessage(genU()),
                onPressUser = {},
                onLongPress = {})
            ChatMessage(
                message = genChatextMessage(genCurrentUser()),
                onPressUser = {},
                onLongPress = {})
        }
    }
}

@Composable
private fun ReplyCount(count: Int) {
    if (count > 0) {
        Text(
            text = "$count replies",
            fontStyle = fonts.body1,
            color = colorScheme.primary,
            modifier = Modifier.padding(4.dp)
        )
    }
}

//@Composable
//private fun Favorite(favorite: Boolean) {
//    if (favorite) {
//        Box(modifier = Modifier.size(35.dp), contentAlignment = Alignment.Center) {
//            Icon(
//                painter = painterResource(Res.drawable.star),
//                contentDescription = "favorite",
//                tint = colorScheme.primary,
//                modifier = Modifier.size(20),
//            )
//        }
//    }
//}

@Composable
private fun Spinner(loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(color = colorScheme.primary, modifier = Modifier.size(20))
    }
}

@Composable
private fun Avvy(url: String?, onClick: () -> Unit) {
    Pressable(onClick = onClick) {
        Avatar(url = url)
    }
}

@Composable
private fun Reactions(msg: Message, modifier: Modifier = Modifier) {
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