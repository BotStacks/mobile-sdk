/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.internal.utils.relativeTimeString
import ai.botstacks.sdk.state.AttachmentType
import ai.botstacks.sdk.state.NotificationSetting
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ai.botstacks.`chat-sdk`.generated.resources.Res
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import dev.icerock.moko.resources.compose.painterResource

/**
 * ChatMessagePreview
 *
 * Renders a "preview" for a given chat. This is based on the last message, if any, that was either
 * sent or received in the chat. Attachments will be rendered as well.
 *
 * This is utilized in [ChatList] to show previews for all chats that a user is currently a member of.
 *
 * @param modifier the Modifier to be applied to this preview.
 * @param chat The chat to extract info from for the preview.
 * @param onClick callback when the contents is clicked.
 *
 */
@Composable
fun ChatMessagePreview(
    modifier: Modifier = Modifier,
    chat: Chat,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
            .padding(BotStacks.dimens.inset, BotStacks.dimens.grid.x4),
        horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x3),
        verticalAlignment = Alignment.CenterVertically) {
        Avatar(url = chat.displayImage)
        Column(
            modifier = Modifier.fillMaxHeight(),
        ) {
            val isMuted by remember(chat.notification_setting) {
                derivedStateOf { chat.notification_setting == NotificationSetting.None }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x2),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = chat.displayName,
                    fontStyle = fonts.label1,
                    color = colorScheme.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Row(horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x3)) {
                    if (isMuted) {
                        Icon(Icons.Rounded.NotificationsOff, contentDescription = "Muted")
                    }

                    chat.latest?.let {
                        Text(
                            text = it.createdAt.relativeTimeString(),
                            fontStyle = fonts.caption2,
                            color = if (chat.isUnread) colorScheme.primary else colorScheme.caption
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val messagePreviewContents = chat.messagePreview
                val text = messagePreviewContents?.first ?: AnnotatedString("No messages yet")
                val contentMap = messagePreviewContents?.second.orEmpty()
                Text(
                    text = text,
                    inlineContent = contentMap,
                    fontStyle = fonts.caption2.copy(
                        weight = if (chat.isUnread) FontWeight.W500 else FontWeight.W400
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = chat.messagePreviewColor
                )
                if (chat.isUnread) {
                    Badge(chat.unreadCount)
                }
            }
        }
    }
}

private val Chat.messagePreviewColor: Color
    @Composable get() = when {
        typingUsers.isNotEmpty() -> colorScheme.primary
        isUnread -> colorScheme.onBackground
        else -> colorScheme.caption
    }

private val Chat.messagePreview: Pair<AnnotatedString, Map<String, InlineTextContent>>?
    @Composable get() = when {
        typingUsers.isNotEmpty() -> AnnotatedString("Typing...") to emptyMap()
        isGroup && latest?.attachments.orEmpty()
            .isEmpty() -> latest?.let { with(it) { AnnotatedString("${user.displayNameFb}: $msg") to emptyMap() } }

        latest?.attachments.orEmpty().isNotEmpty() -> {
            val attachment = latest?.attachments.orEmpty().first()
            when (val type = attachment.type) {
                AttachmentType.Image,
//                AttachmentType.video,
//                AttachmentType.file
                -> buildAnnotatedString {
                    appendInlineContent("imageId")
                    append(" ${type.name}")
                } to mapOf(
                    "imageId" to InlineTextContent(Placeholder(20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter)) {
                        val painter = when (type) {
                            AttachmentType.Image -> rememberVectorPainter(Icons.Rounded.Image)
//                            AttachmentType.video -> rememberVectorPainter(Icons.Rounded.VideoFile)
                            else -> painterResource(Res.images.document_fill)
                        }
                        Image(
                            painter = painter,
                            colorFilter = ColorFilter.tint(if (isUnread) colorScheme.onBackground else colorScheme.caption),
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = null
                        )
                    }
                )

//                AttachmentType.vcard -> AnnotatedString("Shared a contact") to emptyMap()
                AttachmentType.Location -> AnnotatedString("Shared a location") to emptyMap()
//                AttachmentType.audio -> AnnotatedString("Shared audio") to emptyMap()

                AttachmentType.Unknown -> AnnotatedString("Unknown file") to emptyMap()
            }
        }

        else -> latest?.msg?.let { AnnotatedString(it) to emptyMap() }
    }