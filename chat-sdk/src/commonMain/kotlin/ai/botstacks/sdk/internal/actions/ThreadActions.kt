/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.actions

import androidx.compose.runtime.toMutableStateList
import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.state.Upload
import ai.botstacks.sdk.internal.state.toApolloType
import ai.botstacks.sdk.internal.state.toAttachment
import ai.botstacks.sdk.state.*
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.internal.utils.opbg
import ai.botstacks.sdk.internal.utils.uuid
import com.apollographql.apollo3.api.Optional
import kotlinx.datetime.Clock

internal fun AttachmentInput.toAttachment() = FMessage.Attachment(
    id = id,
    url = url,
    data = data.getOrNull(),
    type = type,
    width = width.getOrNull(),
    height = height.getOrNull(),
    duration = duration.getOrNull(),
    latitude = latitude.getOrNull(),
    longitude = longitude.getOrNull(),
    address = address.getOrNull(),
    mime = mime.getOrNull()
)

internal fun MessageAttachment.toInput(): AttachmentInput {
    return AttachmentInput(
        id = id,
        url = url,
        data = Optional.presentIfNotNull(data),
        type = type.toApolloType(),
        width = Optional.presentIfNotNull(width),
        height = Optional.presentIfNotNull(height),
        duration = Optional.presentIfNotNull(duration),
        latitude = Optional.presentIfNotNull(latitude),
        longitude = Optional.presentIfNotNull(longitude),
        address = Optional.presentIfNotNull(address),
        mime = Optional.presentIfNotNull(mime),
    )
}



internal fun Chat.send(
    inReplyTo: String?,
    text: String? = null,
    attachments: List<AttachmentInput>? = null,
    upload: Upload? = null
) {
    val atts = (attachments?.toMutableList() ?: mutableListOf())
    if (upload != null) {
        atts.add(upload.localAttachment())
    }

    val m = Message(
        id = uuid() + "-sending",
        createdAt = Clock.System.now(),
        userID = User.current!!.id,
        parentID = inReplyTo,
        chatID = id,
        _attachments = atts.map { it.toAttachment().toAttachment() },
    )
    m.updateText(text.orEmpty())
    m.upload = upload
    send(m)
}

internal fun Chat.send(sendingMessage: Message) {
    if (!sending.contains(sendingMessage)) {
        sending.add(0, sendingMessage)
        sendingMessage.isSending = true
    }
    if (sendingMessage.parentID == null) {
        latest = sendingMessage
    }

    Monitor.debug("Sending Message")
    op({
        val sm = bg {
            var attachments = sendingMessage.attachments
                .map { it.toInput() }
                .toMutableList()

            if (sendingMessage.upload != null) {
                Monitor.debug("Awaiting upload")
                sendingMessage.upload?.let { upload ->
                    upload.awaitAttachment()?.let { attachment ->
                        Monitor.debug("Got Upload " + attachment.url)
                        val map = attachments.associateBy { it.id }.toMutableMap()
                        map[attachment.id] = attachment.copy(type = upload.attachmentType())

                        attachments = map.values.toMutableList()
                    }
                }
            }

            API.send(
                this@send.id,
                id = sendingMessage.id.removeSuffix("-sending"),
                inReplyTo = sendingMessage.parentID,
                text = sendingMessage.text,
                attachments = attachments
            )
        }
        sm?.let {
            if (sendingMessage.parentID == null) {
                latest = it
            }
            sendingMessage.isSending = false
            sending.remove(sendingMessage)
        } ?: {
            sendingMessage.failed = true
            sendingMessage.isSending = false
            if (sendingMessage.parentID == null) {
                latest = sendingMessage
            }
        }
    }) {
        sendingMessage.failed = true
        sendingMessage.isSending = false
        if (sendingMessage.parentID == null) {
            latest = sendingMessage
        }
    }
}


internal fun Chat.setNotifications(settings: NotificationSetting, isSync: Boolean) {
    val og = this.notification_setting
    this.notification_setting = settings
    if (isSync) {
        return
    }
    op({
        API.updateChatNotifications(id, settings.toApolloType())
    }) {
        this.notification_setting = og
    }
}

internal fun Chat.markRead() {
    unreadCount = 0
    opbg { API.markChatRead(id) }
}