/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op

@Stable
data class ChannelsPager(val list: String = "channels") : Pager<Chat>() {
    override suspend fun load(skip: Int, limit: Int): List<Chat> {
        return API.chats(skip, limit)
    }
}

@Stable
data class UsersPager(val list: String = "users") : Pager<User>() {
    override suspend fun load(skip: Int, limit: Int): List<User> {
        return API.getUsers(skip, limit)
    }
}

@Stable
data class ContactsPager(val list: String = "contacts") : Pager<User>() {

    var requestContacts by mutableStateOf(true)
    var contacts = mutableStateListOf<String>()

    fun fetchContacts(): List<String> {
        return emptyList()
//        val contacts =
//            Contacts(BotStacksChat.shared.appContext).query()
//                .where { Phone.Number.isNotNullOrEmpty() or Phone.NormalizedNumber.isNotNullOrEmpty() }
//                .include {
//                    setOf(
//                        Phone.Number,
//                        Phone.NormalizedNumber
//                    )
//                }
//                .find()
//        return contacts.flatMap { it.phones().map { it.normalizedNumber ?: it.number ?: "" } }
    }

    fun syncContacts() {
        op({
            val contacts = fetchContacts()
            if (!contacts.isEmpty())
                bg {
                    API.getContacts(contacts)
                }
        })
    }

    override suspend fun load(skip: Int, limit: Int): List<User> {
        return API.getUsers(skip, limit)
    }
}

@Stable
data class FavoritesPager(val list: String = "favorites") : Pager<Message>() {
    override suspend fun load(skip: Int, limit: Int): List<Message> {
        return API.favorites(skip, limit)
    }
}

@Stable
data class RepliesPager(val messageId: String) : Pager<Message>(messageId) {

    constructor(message: Message): this(message.id)

    init {
        BotStacksChatStore.current.cache.repliesPagers[messageId] = this
    }

    fun add(message: Message) {
        items.add(0, message)
    }

    override suspend fun load(skip: Int, limit: Int): List<Message> {
        return API.getReplies(messageId, skip, limit)
    }
}

//@Stable
//data class ThreadPager(val id: String = uuid()) : Pager<Message>() {
//    override suspend fun load(skip: Int, limit: Int): List<Message> {
//        return API.getReplyChats(skip, limit)
//    }
//
//}
//
//@Stable
//data class UserSharedMedia(val user: User) : Pager<Message>() {
//    override suspend fun load(skip: Int, limit: Int): List<Message> {
//        return API.getSharedMedia(user.id)
//    }
//
//}
