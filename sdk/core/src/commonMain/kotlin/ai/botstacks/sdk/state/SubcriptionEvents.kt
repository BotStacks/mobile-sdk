package ai.botstacks.sdk.state

import ai.botstacks.sdk.CoreSubscription
import ai.botstacks.sdk.MeSubscription
import ai.botstacks.sdk.fragment.FChat
import ai.botstacks.sdk.fragment.FMember
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.fragment.FUser
import ai.botstacks.sdk.type.DeleteEntity
import ai.botstacks.sdk.type.EntityEventType
import co.touchlab.kermit.Logger

fun BotStacksChatStore.onCreateMessage(it: FMessage) {
    val chat = Chat.get(it.chat_id)
    chat?.let { chat ->
        chat.addMessage(Message.get(it))
    }
}

fun BotStacksChatStore.onCreateMember(it: FMember) {
    val chat = Chat.get(it.chat_id) ?: return

}

fun BotStacksChatStore.onCreateUser(it: FUser) {

}

fun BotStacksChatStore.onCreateChat(it: FChat) {

}

fun BotStacksChatStore.onCoreEvent(event: CoreSubscription.Core) {
    Logger.v("Got Core Event $event")
    event.onDeleteEvent?.fDelete?.let {
        when (it.kind) {
            DeleteEntity.Message -> {
                Message.get(it.id)?.let {
                    cache.messages.remove(it.id)
                    it.chat.items.remove(it)
                    favorites.items.remove(it)
                }
            }

            DeleteEntity.Device -> {
                null
            }

            DeleteEntity.Chat -> {
                Chat.get(it.id)?.let {
                    it.membership?.let {
                        memberships.remove(it)
                    }
                    network.items.remove(it)
                    cache.chats.remove(it.id)
                    if (it.isDM) {
                        it.friend?.let {
                            cache.chatsByUID.remove(it.id)
                        }
                    }
                }
            }

            else -> null
        }
    }
    event.onEntityEvent?.fEntity?.let {
        val isUpdate = it.type == EntityEventType.Update
        it.entity.onChat?.fChat?.let {
            val chat = Chat.get(it)
            if (!isUpdate)
                network.items.add(chat)
        }
        it.entity.onUser?.fUser?.let {
            val user = User.get(it)
            if (!isUpdate)
                contacts.items.add(user)
        }
        it.entity.onMember?.fMember?.let {
            Chat.get(it.chat_id)?.let { chat ->
                val member = Member.get(it)
                if (!isUpdate) {
                    chat.members.add(member)
                } else {
                    chat.members.indexOfFirst { it.user_id == member.user_id }.let {
                        chat.members[it] = member
                    }
                }
            }
        }
        it.entity.onMessage?.fMessage?.let {
            println("On new Message")
            Chat.get(it.chat_id)?.let { chat ->
                println("Have chat for message")
                val message = Message.get(it)
                if (chat.addMessage(message)) {
                    if (Chat.currentlyViewed != message.chatID) {
                        chat.unreadCount += 1
                    }
                }
                if (!isUpdate && (chat.latest == null || chat.latest!!.createdAt < message.createdAt)) {
                    chat.latest = message
                }
            }
        }
    }
}

fun BotStacksChatStore.onMeEvent(event: MeSubscription.Me) {
    Logger.v("Got Me Event $event")
    event.onInviteEvent?.let {
        val chat = Chat.get(it.fInvite.to.fChat)
        val user = User.get(it.fInvite.by.fUser)
        chat.invites.add(user)
        if (!BotStacksChatStore.current.network.items.contains(chat)) {
            BotStacksChatStore.current.network.items.add(chat)
        }
    }
    event.onReactionEvent?.let {
        Message.get(it.fReaction.message.fMessage)
    }
    event.onReplyEvent?.let {
        Message.get(it.fReply.message.fMessage)
    }
}