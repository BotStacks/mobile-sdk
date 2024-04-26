package ai.botstacks.sdk.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

/**
 * Image asset identifier. This is the string asset name normally used in iOS.
 */
actual typealias ImageAssetIdentifier = String

/**
 * Image asset type. [UIImage] for iOS
 */
actual typealias ImageAsset = UIImage

@Composable
internal actual fun painterImageAsset(asset: ImageAssetIdentifier): Painter {
    return BitmapPainter(ImageBitmap.named(asset))
}

@Composable
internal actual fun painterImageAsset(asset: ImageAsset): Painter {
    return BitmapPainter(asset.toComposeImageBitmap())
}

@Composable
internal actual fun ImageAssetIdentifier.toImageAsset(): ImageAsset? {
    return UIImage.imageNamed(this)
}


@OptIn(ExperimentalForeignApi::class)
fun ImageAsset.toComposeImageBitmap() : ImageBitmap {

    val bytes = requireNotNull(UIImagePNGRepresentation(this)) {
        "Failed to get PNG representation of image"
    }

    val byteArray = ByteArray(bytes.length.toInt())

    byteArray.usePinned {
        memcpy(it.addressOf(0), bytes.bytes, bytes.length)
    }

    return Image.makeFromEncoded(byteArray)
        .toComposeImageBitmap()
}

private fun ImageBitmap.Companion.named(name: String) : ImageBitmap {
    return requireNotNull(UIImage.imageNamed(name)) {
        "Image with name $name not found"
    }.toComposeImageBitmap()
}