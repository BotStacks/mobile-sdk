/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

@Stable
data class Caches(
    val users: SnapshotStateMap<String, User> = mutableStateMapOf(),
    val messages: SnapshotStateMap<String, Message> = mutableStateMapOf(),
    val chats: SnapshotStateMap<String, Chat> = mutableStateMapOf(),
    val chatsByUID: SnapshotStateMap<String, Chat> = mutableStateMapOf(),
    val repliesPagers: SnapshotStateMap<String, RepliesPager> = mutableStateMapOf(),
    val chatFetches: MutableSet<String> = mutableSetOf()
)