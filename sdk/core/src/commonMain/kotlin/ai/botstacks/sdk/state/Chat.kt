/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import ai.botstacks.sdk.API
import ai.botstacks.sdk.fragment.FChat
import ai.botstacks.sdk.type.ChatType
import ai.botstacks.sdk.type.MemberRole
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.utils.contains
import ai.botstacks.sdk.utils.op


@Stable
class Chat(id: String, val kind: ChatType) : Pager<Message>(id), Identifiable {

    var name by mutableStateOf<String?>(null)
    var image by mutableStateOf<String?>(null)
    var description by mutableStateOf<String?>(null)
    val members = mutableStateListOf<Member>()
    var _private by mutableStateOf(false)
    val invites = mutableStateListOf<User>()
    var unreadCount by mutableStateOf(0)
    var typingUsers = mutableStateListOf<User>()
    var sending = mutableStateListOf<Message>()

    //    var notification by mutableStateOf(NotificationSetting.all)
    var latest by mutableStateOf<Message?>(null)
    val membership: Member?
        get() = members.find { it.user_id == User.current?.id }
    val isAdmin: Boolean
        get() =
            membership?.role.let { listOf(MemberRole.Admin, MemberRole.Owner).contains(it) }
    val isUnread: Boolean
        get() = unreadCount > 0
    val isMember: Boolean
        get() = membership?.isMember ?: false
    val path: String
        get() = "chat/$id"

    val editPath: String
        get() = "chat/$id/edit"

    val invitePath: String
        get() = "chat/$id/invite"

    val displayImage: String?
        get() = image ?: friend?.avatar
    val displayName: String
        get() {
            return if (kind == ChatType.DirectMessage) friend?.displayNameFb ?: "" else name ?: ""
        }
    val displayDescription: String?
        get() = description ?: friend?.description

    @Stable
    val admins: List<Member>
        get() = members.filter { it.isAdmin }

    @Stable
    val onlineNotAdminUsers: List<Member>
        get() = members.filter { !it.isAdmin && it.user.status != OnlineStatus.Offline }

    @Stable
    val offlineNotAdminUsers: List<Member>
        get() = members.filter { !it.isAdmin && it.user.status == OnlineStatus.Offline }

    val friend: User?
        get() = if (kind == ChatType.DirectMessage) members.find { !it.user.isCurrent }?.user else null

    val isGroup: Boolean
        get() = kind == ChatType.Group

    val isDM: Boolean
        get() = kind == ChatType.DirectMessage

    fun addMessage(message: Message): Boolean {
        val index = items.indexOfFirst { it.id == message.id }
        return if (index >= 0) {
            items[index] = message
            false
        } else {
            items.add(0, message)
            true
        }
    }

    var deleting by mutableStateOf(false)

    var notification_setting by mutableStateOf<NotificationSetting?>(null)
    fun set(notifications: NotificationSetting, isSync: Boolean) {
        this.notification_setting = notifications
        if (isSync) {
            return
        }
        op({
            API.updateChatNotifications(id, notifications)
        })
    }

    override suspend fun load(skip: Int, limit: Int): List<Message> {
        return API.getMessages(id, skip, limit)
    }

    constructor(chat: FChat) : this(chat.id, chat.kind) {
        print("Chat Kind ${chat.kind}")
        update(chat)
    }

    init {
        BotStacksChatStore.current.invites[id]?.let {
            this.invites.addAll(it)
        }
        BotStacksChatStore.current.cache.chats[id] = this
        friend?.let {
            BotStacksChatStore.current.cache.chatsByUID[it.id] = this
        }
    }

    fun update(chat: FChat) {
        this.name = chat.name ?: ""
        this.description = chat.description
        this.image = chat.image
        this._private = chat._private
        chat.members.let {
            this.members.removeAll { true }
            this.members.addAll(it.map { Member.get(it.fMember) })
        }
        if (chat.last_message?.fMessage?.id != latest?.id && chat.last_message != null) {
            val date = latest?.createdAt
            if (date != null && date < chat.last_message.fMessage.created_at)
                return
            latest = Message.get(chat.last_message.fMessage)
        }
        unreadCount = chat.unread_count
    }

    var updating by mutableStateOf(false)
    var joining by mutableStateOf(false)
    var inviting by mutableStateOf(false)

    companion object {
        fun get(id: String) = BotStacksChatStore.current.cache.chats[id]

        fun get(chat: FChat): Chat {
            val g = get(chat.id)
            if (g != null) {
                g.update(chat)
                return g
            }
            return Chat(chat)
        }

        fun getByUser(id: String) = BotStacksChatStore.current.cache.chatsByUID[id]

        var currentlyViewed: String? = null
    }
}