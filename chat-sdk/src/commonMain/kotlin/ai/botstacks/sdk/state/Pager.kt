/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.internal.utils.uuid

interface Identifiable {
    val id: String
}

@Stable
abstract class Pager<T : Identifiable>(
    val id: String = uuid(),
    val items: SnapshotStateList<T> = mutableStateListOf(),
    val pageSize: Int = 20
) {
    var loading by mutableStateOf(false)
    var refreshing by mutableStateOf(false)
    var hasMore by mutableStateOf(true)

    init {
        if (isSinglePage) {
            hasMore = false
        }
    }

    open val isSinglePage: Boolean get() = false

    fun loadMoreIfEmpty() {
        if (items.isEmpty()) {
            if (hasMore)
                loadMore()
            else refresh()
        }
    }

    fun loadMoreIfNeeded(item: T) {
        if (isSinglePage || hasMore) return
        if (items.size < pageSize * 2) {
            loadMore()
            return
        }
        val thresholdIndex = items.size - pageSize
        if (items.indexOf(item) < thresholdIndex) {
            loadMore()
        }
    }

    fun skip(isRefresh: Boolean) = if (isRefresh) 0 else items.size

    fun refresh() {
        if (refreshing) {
            return
        }
        refreshing = true
        loading = true
        val pager = this
        op({
            val items = bg { load(0, pageSize) }
            pager.items.removeAll { true }
            pager.items.addAll(items)
            hasMore = items.size >= pageSize
            loading = false
            refreshing = false
        }, onError = {
            hasMore = false
            loading = false
            refreshing = false
        })
    }

    fun loadMore() {
        if (isSinglePage || !hasMore || refreshing || loading) return
        loading = true
        val pager = this
        op({
            val items = bg { load(items.size, pageSize) }
            pager.items.addAll(items)
            hasMore = items.size >= pageSize
            loading = false
        }, onError = {
            hasMore = false
            loading = false
        })
    }

    abstract suspend fun load(skip: Int, limit: Int): List<T>

}