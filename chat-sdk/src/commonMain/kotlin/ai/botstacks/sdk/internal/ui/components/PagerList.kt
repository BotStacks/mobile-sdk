/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.Identifiable
import ai.botstacks.sdk.state.Pager
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.internal.utils.ui.addIfNonNull
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember


internal object PagerListDefaults {
    @Composable
    fun Divider() {
        Divider(color = colorScheme.caption)
    }
}

enum class ScrollStartPosition {
    BeginningAnimated,
    Beginning,
    EndAnimated,
    End,
    None
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun <T : Identifiable> BotStacksLazyList(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable (() -> Unit)? = null,
    contentHeader: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    listState: LazyListState = rememberLazyListState(),
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: LazyListScope.() -> Unit
) {

    val pullRefreshState = refresh?.let {
        rememberPullRefreshState(refreshing, { refresh() })
    }

    Box(
        modifier = modifier
            .addIfNonNull(pullRefreshState) { Modifier.pullRefresh(it) }
            .fillMaxSize()
    ) {
        Column {
            header?.invoke()
            LazyColumn(
                contentPadding = contentPadding,
                state = listState,
                reverseLayout = invert,
                modifier = Modifier.weight(1f),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                if (!invert) {
                    if (contentHeader != null) {
                        item {
                            contentHeader.invoke()
                        }
                    }
                }

                if (items.isEmpty() && !hasMore) {
                    item { empty() }
                } else {
                    content(this)
                }

                // add last separator
                // this isn't handled by paging separators due to no `beforeItem` to reference against
                // at end of list due to reverseLayout
                if (invert) {
                    (items.getOrNull(items.count() - 1))?.let {
                        item {
                            separator(it, null)
                        }
                    }
                }

                if (invert) {
                    if (contentHeader != null) {
                        item {
                            contentHeader.invoke()
                        }
                    }
                }
            }
            footer?.invoke()
        }

        pullRefreshState?.let {
            PullRefreshIndicator(
                refreshing,
                it,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        if (!(items.isEmpty() && !hasMore)) {
            InfiniteListHandler(listState = listState) {
                loadMore?.invoke()
            }
        }
    }
}

@Composable
internal fun <T : Identifiable> IACList(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    listState: LazyListState = rememberLazyListState(),
    invert: Boolean = false,
    header: @Composable (() -> Unit)? = null,
    contentHeader: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: @Composable LazyItemScope.(T) -> Unit
) {
    BotStacksLazyList(
        modifier = modifier,
        items = items,
        listState = listState,
        invert = invert,
        header = header,
        contentHeader = contentHeader,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        hasMore = hasMore,
        loadMore = loadMore,
        refresh = refresh,
        refreshing = refreshing,
    ) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            Column(verticalArrangement = verticalArrangement) {
                if (!invert) {
                    separator(items.getOrNull(index - 1), item)
                }
                content(item)
                if (invert) {
                    separator(items.getOrNull(index - 1), item)
                }
            }
        }
    }
}

@Composable
internal fun <T : Identifiable> IACListIndexed(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    listState: LazyListState = rememberLazyListState(),
    invert: Boolean = false,
    header: @Composable (() -> Unit)? = null,
    contentHeader: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: @Composable LazyItemScope.(Int, T) -> Unit
) {
    BotStacksLazyList(
        modifier = modifier,
        items = items,
        listState = listState,
        invert = invert,
        header = header,
        contentHeader = contentHeader,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        hasMore = hasMore,
        loadMore = loadMore,
        refresh = refresh,
        refreshing = refreshing,
    ) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            Column(verticalArrangement = verticalArrangement) {
                if (!invert) {
                    separator(items.getOrNull(index - 1), item)
                }
                content(index, item)
                if (invert) {
                    separator(items.getOrNull(index - 1), item)
                }
            }
        }
    }
}

@Composable
internal fun <T : Identifiable> PagerList(
    pager: Pager<T>,
    modifier: Modifier = Modifier,
    prefix: List<T> = listOf(),
    invert: Boolean = false,
    filter: (T) -> Boolean = { true },
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    canRefresh: Boolean = true,
    content: @Composable LazyItemScope.(T) -> Unit
) {
    val array by remember(prefix, pager.items) {
        derivedStateOf { (prefix + pager.items) }
    }

    LaunchedEffect(pager.id) {
        pager.loadMoreIfEmpty()
    }

    val filteredItems by remember(array) {
        derivedStateOf {
            array.filter(filter)
        }
    }

    IACList(
        modifier = modifier,
        items = filteredItems,
        invert = invert,
        header = header,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        hasMore = pager.hasMore,
        loadMore = pager::loadMore,
        refresh = if (canRefresh) {
            { pager.refresh() }
        } else null,
        refreshing = if (canRefresh) pager.refreshing else false,
        content = content
    )
}

@Composable
internal fun <T : Identifiable> PagerListIndexed(
    pager: Pager<T>,
    modifier: Modifier = Modifier,
    prefix: List<T> = listOf(),
    listState: LazyListState = rememberLazyListState(),
    invert: Boolean = false,
    header: @Composable (() -> Unit)? = null,
    contentHeader: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    canRefresh: Boolean = true,
    content: @Composable LazyItemScope.(Int, T) -> Unit
) {
    val array by remember(prefix, pager.items) {
        derivedStateOf { prefix + pager.items }
    }

    LaunchedEffect(pager.id) {
        pager.loadMoreIfEmpty()
    }

    IACListIndexed(
        modifier = modifier,
        items = array,
        listState = listState,
        invert = invert,
        header = header,
        contentHeader = contentHeader,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        hasMore = pager.hasMore,
        loadMore = pager::loadMore,
        refresh = if (canRefresh) {
            { pager.refresh() }
        } else null,
        refreshing = if (canRefresh) pager.refreshing else false,
        content = content
    )
}