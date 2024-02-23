package com.mikepenz.markdown.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size

@Composable
internal fun imagePainter(url: String): Painter? {
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .size(Size.ORIGINAL)
            .build()
    )
}

@Composable
internal fun painterIntrinsicSize(painter: Painter): androidx.compose.ui.geometry.Size {
    var size by remember(painter) { mutableStateOf(painter.intrinsicSize) }

    if (painter is AsyncImagePainter) {
        LaunchedEffect(painter.state) {
            painter.state.painter?.let {
                size = it.intrinsicSize
            }
        }
    }

    return size
}