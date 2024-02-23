/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.actions

import ai.botstacks.sdk.API
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Member
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.type.MemberRole
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.op
import ai.botstacks.sdk.utils.removeIf
import kotlinx.datetime.Clock

fun Chat.dismissInvites() {
    if (invites.isEmpty()) return
    val og = invites.toList()
    invites.removeAll { true }
    op({
        bg {
            API.dismissInvites(id)
        }
    }) {
        invites.addAll(og)
    }
}

fun Chat.join() {
    if (joining || isMember) return
    joining = true
    val p = Member(
        user_id = User.current!!.id,
        chat_id = id,
        created_at = Clock.System.now(),
        role = MemberRole.Member,
    )
    members.add(p)
    op({
        bg {
            API.joinChat(id)
        }
        joining = false
    }) {
        members.remove(p)
        joining = false
    }
}

fun Chat.leave() {
    if (!isMember || joining) return
    joining = true
    if (!isMember) return
    val membership = this.membership
    membership?.let { members.remove(it) }
    op({
        bg {
            API.leaveChat(id)
        }
        joining = false
        BotStacksChatStore.current.memberships.removeIf { it.chat_id == membership?.chat_id }
    }) {
        membership?.let { members.add(it) }
        joining = false
    }
}

fun Chat.update(
    name: String?,
    description: String?,
    image: String?,
    _private: Boolean?,
    onComplete: () -> Unit = {}
) {
    if (updating) {
        return
    }
    updating = true
    val ogName = this.name
    val ogDescription = this.description
    val ogImage = this.image
    val ogPrivate = this._private
    name?.let {
        this.name = it
    }
    description?.let {
        this.description = it
    }
    image?.let {
        this.image = it
    }
    _private?.let {
        this._private = it
    }
    op({
        bg {
            API.updateChat(
                id = id,
                name = name.orEmpty(),
                description = description,
                image = image,
                private = _private ?: false,
            )
        }
        updating = false
        onComplete()
    }) {
        this.name = ogName
        this.description = ogDescription
        this.image = ogImage
        this._private = ogPrivate
        updating = false
    }
}

fun Chat.delete() {
    op({
        bg { API.deleteChat(id) }
        friend?.let { BotStacksChatStore.current.cache.chatsByUID.remove(it.id) }
        BotStacksChatStore.current.cache.chats.remove(id)
        BotStacksChatStore.current.network.items.removeAll { it.id == id }
        BotStacksChatStore.current.memberships.removeAll { it.chat_id == id }
    })
}

fun Chat.invite(users: List<User>) {
    if (inviting) {
        return
    }
    inviting = true
    op({
        bg {
            API.invite(users.map { it.id }, id)
        }
        inviting = false
    }) {
        inviting = false
    }
}