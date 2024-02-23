package ai.botstacks.sdk.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

actual typealias ImageAssetIdentifier = Int

actual typealias ImageAsset = ImageVector

@Composable
actual fun painterImageAsset(asset: ImageAssetIdentifier): Painter {
    return painterResource(id = asset)
}

@Composable
actual fun painterImageAsset(asset: ImageAsset): Painter {
    return rememberVectorPainter(image = asset)
}

@Composable
actual fun ImageAssetIdentifier.toImageAsset(): ImageAsset? {
    return ImageVector.vectorResource(id = this)
}

