package ai.botstacks.sdk.internal.state

import ai.botstacks.sdk.CoreSubscription
import ai.botstacks.sdk.MeSubscription
import ai.botstacks.sdk.fragment.FChat
import ai.botstacks.sdk.fragment.FMember
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.fragment.FUser
import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Participant
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.type.DeleteEntity
import ai.botstacks.sdk.type.EntityEventType

internal fun BotStacksChatStore.onCreateMessage(it: FMessage) {
    val chat = Chat.get(it.chat_id)
    chat?.let { chat ->
        chat.addMessage(Message.get(it))
    }
}

internal fun BotStacksChatStore.onCreateMember(it: FMember) {
    val chat = Chat.get(it.chat_id) ?: return

}

internal fun BotStacksChatStore.onCreateUser(it: FUser) {

}

internal fun BotStacksChatStore.onCreateChat(it: FChat) {

}

internal fun BotStacksChatStore.onCoreEvent(event: CoreSubscription.Core) {
    if (!BotStacksChatStore.current.receivedEvents.contains(event)) {
        Monitor.debug("Got Core Event $event")
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
                    val member = Participant.get(it)
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
                Monitor.debug("On new Message")
                Chat.get(it.chat_id)?.let { chat ->
                    Monitor.debug("Have chat for message")
                    val message = Message.get(it)
                    if (chat.addMessage(message)) {
                        if (Chat.currentlyViewed != message.chatID) {
                            chat.unreadCount += 1
                        }
                    }
                    if (message.parentID == null) {
                        if (!isUpdate && (chat.latest == null || chat.latest!!.createdAt < message.createdAt)) {
                            chat.latest = message
                        }
                    }
                }
            }
        }
        BotStacksChatStore.current.receivedEvents.add(event)
    }
}

internal fun BotStacksChatStore.onMeEvent(event: MeSubscription.Me) {
    Monitor.debug("Got Me Event $event")
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