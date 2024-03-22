/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.state

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.utils.uuid
import ai.botstacks.sdk.state.ChannelsPager
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.ChatType
import ai.botstacks.sdk.state.ContactsPager
import ai.botstacks.sdk.state.FavoritesPager
import ai.botstacks.sdk.state.Participant
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.state.UsersPager
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.properties.Delegates

@Stable
internal data class BotStacksChatStore(val id: String = uuid()) {

    //    val messages = ThreadPager()
    val favorites = FavoritesPager()
    val settings = Settings()
    val network = ChannelsPager()
    val contacts = ContactsPager()
    val users = UsersPager()
    val invites = mutableStateMapOf<String, MutableList<User>>()
    val cache = Caches()
    val memberships = mutableStateListOf<Participant>()
    val chats: List<Chat>
        get() = memberships.filter { it.isMember }
            .map { it.chat }
    val dms: List<Chat>
        get() = chats.filter { it.kind == ChatType.DirectMessage }
    val groups: List<Chat>
        get() = chats.filter { it.kind == ChatType.Group }

    var fcmToken: String? = null

    var loading by mutableStateOf(false)

    var currentUserID: String? by Delegates.observable(
        BotStacksChat.shared.prefs.getStringOrNull(
            "user-id",
        )
    ) { _, _, newValue ->
        if (newValue != null) {
            BotStacksChat.shared.prefs.putString("user-id", newValue)
        }
    }

    var user by mutableStateOf<User?>(null)

    fun init() {
        API.init()
        settings.init()
    }

    suspend fun loadAsync() {
        currentUserID ?: return
        val user = API.me()
        Monitor.debug("user id ${user.id}")
        User.current = user
        val fcmToken = this.fcmToken
        if (fcmToken != null) {
            API.registerFcmToken(fcmToken)
        }
        loadInvites()
    }

    private suspend fun loadInvites() {
        val invites = API.invites()
        for (invite in invites) {
            val chat = Chat.get(invite.chat.fChat)
            val user = User.get(invite.user.fUser)
            if (!chat.invites.contains(user))
                chat.invites.add(user)
        }
    }

    enum class ChatList(val label: String) {
        groups("Channels"),
        dms("Chat");

        companion object {
            fun forRoute(r: String?): ChatList {
                if (r == null) return dms
                when (r) {
                    "channels" -> groups
                    "dms" -> dms
                }
                return dms
            }
        }
    }

    fun count(list: ChatList): Int =
        when (list) {
            ChatList.dms -> {
                val it = dms.sumOf { it.unreadCount }
                Monitor.debug("Dms unread count $it")
                it
            }

            ChatList.groups -> {
                val it = groups.sumOf { it.unreadCount }
                Monitor.debug("groups unread count $it")
                it
            }
        }

    val totalCount: Int
        get() = dms.sumOf { it.unreadCount } + groups.sumOf { it.unreadCount }
    var nextGif: ((String) -> Unit)? = null


    @Stable
    companion object {
        var current = BotStacksChatStore()
    }
}