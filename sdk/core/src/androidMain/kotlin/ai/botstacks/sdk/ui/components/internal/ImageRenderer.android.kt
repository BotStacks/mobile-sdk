package ai.botstacks.sdk.ui.components.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal actual fun ImageRenderer(
    modifier: Modifier,
    contentDescription: String?,
    contentScale: ContentScale,
    url: String,
    onClick: (() -> Unit)?,
    onLongClick: () -> Unit,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier.combinedClickable(onClick = { onClick?.invoke()}, onLongClick = onLongClick),
        onError = {
            it.result.throwable.printStackTrace()
        }
    )
}