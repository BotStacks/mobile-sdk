package ai.botstacks.sdk.utils

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class Giphy
expect class GiphyModal
expect class GiphyThemeType
expect class GiphyContentType

expect fun GiphyModal.setStickerColumnCount(@IntRange(from = 2, to = 4) count: Int)
expect fun GiphyModal.setMediaType(@IntRange(from = 0, to = 5) contentType: GiphyContentType)
expect fun GiphyModal.setTheme(@IntRange(from = 0, to = 2) themeType: GiphyThemeType)

@Composable
expect fun GiphyModalSheet(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSelection: (String) -> Unit
)