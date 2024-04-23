package ai.botstacks.sdk.internal.utils.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable

internal fun LazyListScope.animatedItem(
    key: Any? = null,
    contentType: Any? = null,
    visible: Boolean,
    enter: EnterTransition? = null,
    exit: ExitTransition? = null,
    content: @Composable LazyItemScope.() -> Unit
) {
    item(key = key, contentType = contentType) {
        if (enter != null && exit != null) {
            AnimatedVisibility(
                visible = visible,
                enter = enter,
                exit = exit,
            ) {
                content()
            }
        } else {
            AnimatedVisibility(
                visible = visible,
            ) {
                content()
            }
        }
    }
}

internal fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

internal fun LazyListState.isScrolledToTheBeginning() =
    (layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0) == 0