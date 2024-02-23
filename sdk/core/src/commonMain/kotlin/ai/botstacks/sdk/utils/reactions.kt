package ai.botstacks.sdk.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList


typealias Reactions = SnapshotStateList<Pair<String, SnapshotStateList<String>>>


fun parseReactions(reactions: String?): Reactions? {
    if (reactions == null) return null
    return reactions.split(";").map { reaction ->
        val split = reaction.split(":")
        if (split.count() == 2) {
            (split[0] to split[1].split(",").toMutableStateList())
        } else {
            Pair("", mutableStateListOf())
        }
    }.toMutableStateList()
}

fun addReaction(reactions: Reactions, uid: String, reaction: String) {
    reactions.firstOrNull { it.first == reaction }
        ?.second
        ?.add(reaction)
        ?: reactions.add((reaction to mutableStateListOf(uid)))
}

fun removeReaction(reactions: Reactions, index: Int, uid: String) {
    reactions[index].second.removeIf { it != uid }
    if (reactions[index].second.isEmpty()) {
        reactions.removeAt(index)
    }
}

fun findUserReactionIndex(reactions: Reactions, uid: String): Int {
    return reactions.indexOfFirst { it.second.contains(uid) }
}

fun react(
    uid: String,
    reaction: String,
    reactions: Reactions
) {
    val index = findUserReactionIndex(reactions, uid)
    if (index > -1) {
        if (reaction == reactions[index].first) {
            removeReaction(reactions, index, uid)
        } else {
            removeReaction(reactions, index, uid)
            addReaction(reactions, uid, reaction)
        }
    } else {
        addReaction(reactions, uid, reaction)
    }
}