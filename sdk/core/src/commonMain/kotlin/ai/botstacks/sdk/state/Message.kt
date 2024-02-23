/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import ai.botstacks.sdk.API
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.type.AttachmentType
import ai.botstacks.sdk.utils.*
import kotlinx.datetime.Instant

@Stable
data class Message(
    override val id: String,
    val createdAt: Instant,
    val userID: String,
    val parentID: String?,
    val chatID: String,
    val attachments: SnapshotStateList<FMessage.Attachment> = mutableStateListOf(),
    val reactions: Reactions = mutableStateListOf()
) : Identifiable {
    var text by mutableStateOf("")
    var markdown by mutableStateOf("")
    var replyCount by mutableStateOf(0)
    var favorite by mutableStateOf(false)
    var currentReaction by mutableStateOf<String?>(null)
    var parent by mutableStateOf<Message?>(null)

    val replies by lazy { RepliesPager(this) }
    val user: User
        get() = User.get(userID)!!
    val chat: Chat
        get() = Chat.get(chatID)!!
    val path: String get() = "message/$id"

    constructor(msg: FMessage) : this(
        msg.id,
        msg.created_at,
        msg.user.fUser.id,
        msg.parent_id,
        msg.chat_id,
        msg.attachments?.toMutableStateList() ?: mutableStateListOf(),
        msg.reactions?.let { parseReactions(it) } ?: mutableStateListOf()
    ) {
        update(msg)
    }

    init {
        BotStacksChatStore.current.cache.messages[id] = this
        parentID?.let { parentId ->
            get(parentId)?.let { parent = it } ?: op({
                parent = bg { API.getMessage(parentId) }
            })
        }
    }

    fun updateText(text: String) {
        this.text = text
        this.markdown = linkLinks(linkPhones(linkMentions(this.text)))
    }

    fun update(msg: FMessage) {
        if (this.text != (msg.text ?: "")) {
            updateText(msg.text ?: "")
        }
        this.replyCount = msg.reply_count
//        this.favorite = msg ?: this.favorite
        msg.reactions?.let { parseReactions(it) }?.let {
            this.reactions.removeAll { true }
            this.reactions.addAll(it)
        }
        this.currentReaction =
            reactions.find { it.second.contains(User.current!!.id) }?.first
    }

    val msg: String
        get() = attachments.firstOrNull()?.let {
            when (it.type) {
                AttachmentType.image -> "[Image] ${it.url}"
                AttachmentType.video -> "[Video] ${it.url}"
                AttachmentType.audio -> "[Audio] ${it.url}"
                AttachmentType.file -> "[File] ${it.url}"
                AttachmentType.location -> "[Location] ${it.latitude ?: 0.0},${it.longitude ?: 0.0}"
                AttachmentType.vcard -> "[Contact]"
                else -> null
            }
        } ?: text


    var reacting by mutableStateOf(false)
    var favoriting by mutableStateOf(false)
    var editingText by mutableStateOf(false)

    var upload: Upload? = null
    var failed by mutableStateOf(false)
    var isSending by mutableStateOf(false)

    companion object {
        fun get(id: String): Message? {
            return BotStacksChatStore.current.cache.messages[id]
        }

        fun get(apiMessage: FMessage): Message {
            User.get((apiMessage.user.fUser))
            val m = get(apiMessage.id)
            if (m != null) {
                m.update(apiMessage)
                return m
            }
            return Message(apiMessage)
        }
    }
}

fun FMessage.Attachment.vcard() =
    if (type == AttachmentType.vcard) parseVcard(data) else null

fun FMessage.Attachment.location() =
    if (type == AttachmentType.location) Location(latitude, longitude, address) else null

fun FMessage.Attachment.file() =
    if (type == AttachmentType.file) File(data = data.orEmpty(), mimeString = mime.orEmpty()) else null

data class Location(val latitude: Double?, val longitude: Double?, val address: String? = null) {
    val link: String
        get() = "https://www.google.com/maps/search/?api=1&query=" + urlEncode(
            address ?: "${latitude!!},${longitude!!}", "utf-8"
        )
    val markdown: String
        get() = "[Location${address?.let { ": $it" } ?: ""}](${link})"

}

data class File(val data: String, val mimeString: String)

