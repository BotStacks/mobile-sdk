/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.actions

import ai.botstacks.sdk.API
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.op
import androidx.compose.runtime.toMutableStateList


fun Message.react(emoji: String) {
    if (reacting) return
    reacting = true
    val og = reactions.toList().map { it.first to it.second.toList() }
    val ogCurrent = currentReaction
    ai.botstacks.sdk.utils.react(User.current!!.id, emoji, reactions)
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


fun Message.toggleFavorite() {
    if (favoriting) return
    favoriting = true
    favorite = !favorite
    op({
        bg {
            API.favorite(id, !favorite)
        }
        favoriting = false
    }) {
        favorite = !favorite
        favoriting = false
    }
}

fun Message.editText(newText: String) {
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

fun Message.delete() {
    parent?.replies?.items?.remove(this)
    chat.items.remove(this)
    BotStacksChatStore.current.cache.messages.remove(id)
    op({
        bg { API.deleteMessage(id) }
    })
}

fun Message.retry() {
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