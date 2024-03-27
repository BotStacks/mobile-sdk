package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.state.OnlineStatus
import ai.botstacks.sdk.ui.theme.toComposeImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import platform.UIKit.UIImage

object BSKAvatarType {
    fun user(
        url: Any?,
        status: OnlineStatus = OnlineStatus.Unknown,
        empty: UIImage? = null,
    ): AvatarType.User {
        return AvatarType.User(
            url = url,
            status = status,
            empty = empty?.let { BitmapPainter(empty.toComposeImageBitmap()) }
        )
    }

    fun channel(
        urls: List<String?>,
        empty: UIImage? = null,
    ): AvatarType.Channel {
        return AvatarType.Channel(
            urls = urls,
            empty = empty?.let { BitmapPainter(empty.toComposeImageBitmap()) }
        )
    }
}
