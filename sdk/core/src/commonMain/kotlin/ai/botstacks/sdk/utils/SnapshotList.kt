package ai.botstacks.sdk.utils

import androidx.compose.runtime.snapshots.SnapshotStateList

fun <T> SnapshotStateList<T>.removeIf(predicate: (T) -> Boolean) {
    val removals = filter { predicate(it) }
    removeAll(removals)
}