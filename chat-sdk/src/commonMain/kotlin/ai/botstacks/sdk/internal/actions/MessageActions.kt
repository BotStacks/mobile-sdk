/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.actions

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op
import androidx.compose.runtime.toMutableStateList


internal fun Message.react(emoji: String) {
    if (reacting) return
    reacting = true
    val og = reactions.toList().map { it.first to it.second.toList() }
    val ogCurrent = currentReaction
    ai.botstacks.sdk.internal.utils.react(User.current!!.id, emoji, reactions)
    op({
        bg {
            API.react(id, emoji)
        }
        reacting = false
    }) {
        reactions.removeAll { true }
        reactions.addAll(og.map { it.first to it.second.toMutableStateList() })
        currentReaction = ogCurrent
        reacting = false
    }
}


internal fun Message.toggleFavorite() {
    if (favoriting) return
    favoriting = true
    favorite = !favorite
    op({
        bg {
            API.favorite(id, !favorite)
        }
        favoriting = false
        BotStacksChatStore.current.favorites.refresh()
    }) {
        favorite = !favorite
        favoriting = false
        BotStacksChatStore.current.favorites.refresh()
    }
}

internal fun Message.editText(newText: String) {
    if (editingText) return
    editingText = true
    val og = text
    text = newText
    op({
        bg {
            API.editMessageText(id, newText)
        }
        editingText = false
    }) {
        text = og
        editingText = false
    }
}

internal fun Message.delete() {
    parent?.replies?.items?.remove(this)
    chat.items.remove(this)
    BotStacksChatStore.current.cache.messages.remove(id)
    op({
        bg { API.deleteMessage(id) }
    })
}

internal fun Message.retry() {
    if (failed) {
        failed = false
        upload?.let {
            if (it.error != null) {
                it.upload()
            }
        }
        chat.send(this)
    }
}