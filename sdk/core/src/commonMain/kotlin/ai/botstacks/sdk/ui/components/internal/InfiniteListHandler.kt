/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components.internal

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * Handler to make any lazy column (or lazy row) infinite. Will notify the [onLoadMore]
 * callback once needed
 * @param listState state of the list that needs to also be passed to the LazyColumn composable.
 * Get it by calling rememberLazyListState()
 * @param buffer the number of items before the end of the list to call the onLoadMore callback
 * @param onLoadMore will notify when we need to load more
 */
@Composable
internal fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 20,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(buffer) {
        snapshotFlow { listState.layoutInfo }
            .map { layoutInfo ->
                val totalItemsNumber = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

                lastVisibleItemIndex > (totalItemsNumber - buffer)
            }.filter { it }
            .onEach { onLoadMore() }
            .launchIn(this)
    }
}