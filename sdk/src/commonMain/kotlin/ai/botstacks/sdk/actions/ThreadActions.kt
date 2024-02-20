/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.actions

import androidx.compose.runtime.toMutableStateList
import ai.botstacks.sdk.API
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.state.*
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.op
import ai.botstacks.sdk.utils.opbg
import ai.botstacks.sdk.utils.uuid
import com.apollographql.apollo3.api.Optional
import kotlinx.datetime.Clock

fun AttachmentInput.toAttachment() = FMessage.Attachment(
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

fun FMessage.Attachment.toInput(): AttachmentInput {
    return AttachmentInput(
        id = id,
        url = url,
        data = Optional.presentIfNotNull(data),
        type = type,
        width = Optional.presentIfNotNull(width),
        height = Optional.presentIfNotNull(height),
        duration = Optional.presentIfNotNull(duration),
        latitude = Optional.presentIfNotNull(latitude),
        longitude = Optional.presentIfNotNull(longitude),
        address = Optional.presentIfNotNull(address),
        mime = Optional.presentIfNotNull(mime),
    )
}

fun Chat.send(
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
        id = uuid(),
        createdAt = Clock.System.now(),
        userID = User.current!!.id,
        parentID = inReplyTo,
        chatID = id,
        attachments = atts.map { it.toAttachment() }.toMutableStateList(),
    )
    m.text = text ?: ""
    send(m)
}

fun Chat.send(sendingMessage: Message) {
    if (!sending.contains(sendingMessage)) {
        sending.add(0, sendingMessage)
        sendingMessage.isSending = true
    }
    print("Sending Message")
    op({
        val sm = bg {
            val attachments = sendingMessage.attachments
                .map { it.toInput() }
                .toMutableList()

            if (sendingMessage.upload != null) {
                print("Awaiting upload")
                sendingMessage.upload?.let {
                    it.awaitAttachment()?.let { attachment ->
                        println("Got Upload " + attachment.url)
                        attachments.add(attachment)
                    }
                }
            }
            println("Sending")
            API.send(
                id,
                id = sendingMessage.id,
                inReplyTo = sendingMessage.parentID,
                text = sendingMessage.text,
                attachments = attachments
            )
        }
        sm?.let {
            items.add(0, it)
            sending.remove(sendingMessage)
        } ?: {
            sendingMessage.failed = true
            sendingMessage.isSending = false
        }
    }) {
        sendingMessage.failed = true
        sendingMessage.isSending = false
    }
}


fun Chat.setNotifications(settings: NotificationSetting, isSync: Boolean) {
    val og = this.notification_setting
    this.notification_setting = settings
    if (isSync) {
        return
    }
    op({
        API.updateChatNotifications(id, settings)
    }) {
        this.notification_setting = og
    }
}

fun Chat.markRead() {
    unreadCount = 0
    opbg({
        API.markChatRead(id)
    })
}