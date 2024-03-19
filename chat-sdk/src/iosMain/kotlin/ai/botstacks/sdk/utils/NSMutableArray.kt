package ai.botstacks.sdk.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import platform.Foundation.NSMutableArray

fun <T> NSMutableArray.snapshot(): SnapshotStateList<T> {
    return (this as MutableList<T>).toMutableStateList()
}