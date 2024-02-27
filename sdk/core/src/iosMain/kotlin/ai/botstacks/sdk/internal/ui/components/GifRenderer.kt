package ai.botstacks.sdk.internal.ui.components

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.utils.launch
import ai.botstacks.sdk.internal.utils.ui.contentDescription
import ai.botstacks.sdk.internal.utils.ui.gifImageWithURL
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.cinterop.useContents
import platform.UIKit.UIImage
import platform.UIKit.UIImageView

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun GifRenderer(
    modifier: Modifier,
    contentDescription: String?,
    contentScale: ContentScale,
    url: String,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?
) {
    val gifView = remember(url) {
        UIImageView().apply {
            launch {
                image = UIImage.gifImageWithURL(url)
            }
        }
    }

    Box(
        modifier = modifier.contentDescription(contentDescription),
    ) {
        UIKitView(
            modifier = Modifier
                .fillMaxWidth()
                .height(BotStacks.dimens.imagePreviewSize.height.dp),
            factory = { gifView },
            interactive = true,
            onResize = { view, size ->
                view.layer.setFrame(size)
            },
            update = { it.startAnimating() }
        )
        Box(
            modifier = Modifier
                .zIndex(100f)
                .fillMaxSize()
                .combinedClickable(onClick = { onClick?.invoke()}, onLongClick = onLongClick),
            )
    }
}
