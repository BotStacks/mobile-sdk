package ai.botstacks.sdk.internal.ui.components

import ai.botstacks.sdk.internal.Monitor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreFoundation.CFDataRef
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithRequest
import platform.ImageIO.CGImageSourceCreateWithData
import platform.ImageIO.CGImageSourceGetCount
import kotlin.coroutines.resume

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal actual fun ImageRenderer(
    modifier: Modifier,
    contentDescription: String?,
    contentScale: ContentScale,
    url: String,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
) {

    var isAnimated by remember { mutableStateOf<Boolean?>(null) }
    when (isAnimated) {
        true -> GifRenderer(
            modifier = modifier,
            contentDescription = contentDescription,
            contentScale = contentScale,
            url = url,
            onClick = onClick,
            onLongClick = onLongClick,
        )
        false -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier.combinedClickable(onClick = { onClick?.invoke()}, onLongClick = onLongClick),
            )
        }
        else -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(url) {
        isAnimated = isAnimated(url)
    }
}

private suspend fun isAnimated(url: String): Boolean = suspendCancellableCoroutine { cont ->
    runCatching {
        val request = NSURLRequest.requestWithURL(NSURL(string = url),)
        NSURLSession.sharedSession.dataTaskWithRequest(
            request = request,
            completionHandler = { data, response, error ->
                if (error == null && response != null && data != null) {
                    if ((response as NSHTTPURLResponse).statusCode == 200L) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val dataRef = CFBridgingRetain(data) as? CFDataRef
                            val source = CGImageSourceCreateWithData(dataRef, null)
                            val count = CGImageSourceGetCount(source)
                            cont.resume(count > 1u)
                            return@launch
                        }
                    }
                } else {
                    cont.resume(false)
                }
            }
        ).resume()
    }.onFailure { cont.resume(false) }

}
