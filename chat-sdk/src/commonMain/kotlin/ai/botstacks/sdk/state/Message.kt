/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.state.Upload
import ai.botstacks.sdk.internal.state.toAttachment
import ai.botstacks.sdk.internal.utils.Reactions
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.linkLinks
import ai.botstacks.sdk.internal.utils.linkMentions
import ai.botstacks.sdk.internal.utils.linkPhones
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.internal.utils.parseReactions
import kotlinx.datetime.Instant

/**
 * A representation of a Message in a given [Chat].
 */
@Stable
data class Message(
    override val id: String,
    internal val createdAt: Instant,
    internal val userID: String,
    internal val parentID: String?,
    internal val chatID: String,
    private val _attachments: List<MessageAttachment> = emptyList(),
    internal val _reactions: List<Pair<String, List<String>>> = emptyList()
) : Identifiable {
    var text by mutableStateOf("")
        internal set
    var markdown by mutableStateOf("")
        internal set
    var replyCount by mutableStateOf(0)
    var favorite by mutableStateOf(false)
    var currentReaction by mutableStateOf<String?>(null)
    var parent by mutableStateOf<Message?>(null)

    internal val attachments: SnapshotStateList<MessageAttachment> = _attachments.toMutableStateList()
    internal val reactions: Reactions = _reactions.map { it.first to it.second.toMutableStateList() }.toMutableStateList()

    val isGroup = Chat.get(chatID)?.isGroup ?: false

    val replies by lazy { RepliesPager(this) }
    val user: User
        get() = User.get(userID) ?: throw IllegalStateException()
    val chat: Chat
        get() = Chat.get(chatID)  ?: throw IllegalStateException()
    val path: String get() = "message/$id"

    internal constructor(msg: FMessage) : this(
        msg.id,
        msg.created_at,
        msg.user.fUser.id,
        msg.parent_id,
        msg.chat_id,
        msg.attachments?.map { it.toAttachment() }?.toMutableStateList() ?: mutableStateListOf(),
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

    internal fun update(msg: FMessage) {
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
                AttachmentType.Image -> "[Image] ${it.url}"
//                AttachmentType.video -> "[Video] ${it.url}"
//                AttachmentType.audio -> "[Audio] ${it.url}"
//                AttachmentType.file -> "[File] ${it.url}"
                AttachmentType.Location -> "[Location] ${it.latitude ?: 0.0},${it.longitude ?: 0.0}"
//                AttachmentType.vcard -> "[Contact]"
                else -> null
            }
        } ?: text


    var reacting by mutableStateOf(false)
    var favoriting by mutableStateOf(false)
    var editingText by mutableStateOf(false)

    internal var upload: Upload? = null
    var failed by mutableStateOf(false)
    var isSending by mutableStateOf(false)

    companion object {
        internal fun get(id: String): Message? {
            return BotStacksChatStore.current.cache.messages[id]
        }

        internal fun get(apiMessage: FMessage): Message {
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

