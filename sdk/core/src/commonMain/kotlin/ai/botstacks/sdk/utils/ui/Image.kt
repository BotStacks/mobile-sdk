package ai.botstacks.sdk.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

expect class ImageAssetIdentifier
expect class ImageAsset

@Composable
expect fun ImageAssetIdentifier.toImageAsset(): ImageAsset?

@Composable
expect fun painterImageAsset(asset: ImageAssetIdentifier): Painter

@Composable
expect fun painterImageAsset(asset: ImageAsset): Painter