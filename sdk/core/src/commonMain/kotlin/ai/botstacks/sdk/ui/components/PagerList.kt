/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.Identifiable
import ai.botstacks.sdk.state.Pager
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.components.internal.InfiniteListHandler
import ai.botstacks.sdk.utils.Fn
import ai.botstacks.sdk.utils.ui.addIfNonNull
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object PagerListDefaults {
    @Composable
    fun Divider() {
        Divider(color = colorScheme.caption)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Identifiable> BotStacksLazyList(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable Fn? = null,
    footer: @Composable Fn? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollToTop: String? = null,
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: LazyListScope.() -> Unit
) {

    val pullRefreshState = refresh?.let {
        rememberPullRefreshState(refreshing, { refresh() })
    }

    if (items.isEmpty() && !hasMore) {
        Box(
            modifier = modifier
                .addIfNonNull(pullRefreshState) { Modifier.pullRefresh(it) }
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                header?.invoke()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    empty()
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
        }

    } else {
        Box(
            modifier = modifier
                .addIfNonNull(pullRefreshState) { Modifier.pullRefresh(it) }
                .fillMaxSize()
        ) {
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            LazyColumn(
                contentPadding = contentPadding,
                state = listState,
                reverseLayout = invert,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                header?.let { item { it() } }
                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colorScheme.primary)
                        }
                    }
                }

                content(this)

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
            }

            pullRefreshState?.let {
                PullRefreshIndicator(
                    refreshing,
                    it,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            InfiniteListHandler(listState = listState) {
                loadMore?.invoke()
            }

            LaunchedEffect(key1 = scrollToTop, block = {
                delay(300)
                coroutineScope.launch { listState.animateScrollToItem(0) }
            })
        }
    }
}

@Composable
fun <T : Identifiable> IACList(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable Fn? = null,
    footer: @Composable Fn? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollToTop: String? = null,
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: @Composable LazyItemScope.(T) -> Unit
) {
    BotStacksLazyList(
        modifier = modifier,
        items = items,
        invert = invert,
        header = header,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        scrollToTop = scrollToTop,
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
fun <T : Identifiable> IACListIndexed(
    modifier: Modifier = Modifier,
    items: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable Fn? = null,
    footer: @Composable Fn? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollToTop: String? = null,
    hasMore: Boolean = false,
    loadMore: (() -> Unit)? = null,
    refresh: (() -> Unit)? = null,
    refreshing: Boolean = false,
    content: @Composable LazyItemScope.(Int, T) -> Unit
) {
    BotStacksLazyList(
        modifier = modifier,
        items = items,
        invert = invert,
        header = header,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        scrollToTop = scrollToTop,
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
fun <T : Identifiable> PagerList(
    pager: Pager<T>,
    modifier: Modifier = Modifier,
    prefix: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable Fn? = null,
    footer: @Composable Fn? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollToTop: String? = null,
    canRefresh: Boolean = true,
    content: @Composable LazyItemScope.(T) -> Unit
) {
    val array by remember(prefix, pager.items) {
        derivedStateOf { (prefix + pager.items) }
    }

    LaunchedEffect(pager.id) {
        pager.loadMoreIfEmpty()
    }

    IACList(
        modifier = modifier,
        items = array,
        invert = invert,
        header = header,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        scrollToTop = scrollToTop,
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
fun <T : Identifiable> PagerListIndexed(
    pager: Pager<T>,
    modifier: Modifier = Modifier,
    prefix: List<T> = listOf(),
    invert: Boolean = false,
    header: @Composable Fn? = null,
    footer: @Composable Fn? = null,
    empty: @Composable () -> Unit = {},
    separator: @Composable (T?, T?) -> Unit = { _, _ -> },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollToTop: String? = null,
    canRefresh: Boolean = true,
    content: @Composable LazyItemScope.(Int, T) -> Unit
) {
    val array = prefix + pager.items
    LaunchedEffect(pager.id) {
        pager.loadMoreIfEmpty()
    }

    IACListIndexed(
        modifier = modifier,
        items = array,
        invert = invert,
        header = header,
        footer = footer,
        empty = empty,
        separator = separator,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        scrollToTop = scrollToTop,
        hasMore = pager.hasMore,
        loadMore = pager::loadMore,
        refresh = if (canRefresh) {
            { pager.refresh() }
        } else null,
        refreshing = if (canRefresh) pager.refreshing else false,
        content = content
    )
}