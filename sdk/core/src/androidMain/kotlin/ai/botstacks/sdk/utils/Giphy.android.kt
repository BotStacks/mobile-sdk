package ai.botstacks.sdk.utils

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.utils.imageWithRenditionType
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup

actual typealias Giphy = Giphy
actual typealias GiphyModal = GiphyDialogView
actual typealias GiphyThemeType = Int
actual typealias GiphyContentType = Int

object GiphyConfig {
    var settings = GPHSettings()
        internal set
}


actual fun GiphyModal.setStickerColumnCount(@IntRange(from = 2, to = 4) count: Int) {
    GiphyConfig.settings = GiphyConfig.settings.apply {
        stickerColumnCount = count
    }
}

actual fun GiphyModal.setTheme(@IntRange(from = 0, to = 2) themeType: GiphyThemeType) {
    GiphyConfig.settings = GiphyConfig.settings.apply {
        theme = GPHTheme.entries[themeType]
    }
}

actual fun GiphyModal.setMediaType(@IntRange(from = 0, to = 5) contentType: GiphyContentType) {
    GiphyConfig.settings = GiphyConfig.settings.apply {
        val type = GPHContentType.entries[contentType]
        mediaTypeConfig = arrayOf(type)
    }
}

@Composable
actual fun GiphyModalSheet(
    modifier: Modifier,
    onCancel: () -> Unit,
    onSelection: (String) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            val settings =
                GPHSettings(theme = GPHTheme.Automatic, stickerColumnCount = 3)
            settings.mediaTypeConfig = arrayOf(GPHContentType.gif)
            GiphyDialogView(ctx).apply {
                setup(settings)
                listener = object : GiphyDialogView.Listener {
                    override fun didSearchTerm(term: String) = Unit

                        override fun onClosed(selectedContentType: GPHContentType) = Unit

                        override fun onFocusSearch() = Unit

                    override fun onGifSelected(
                        media: com.giphy.sdk.core.models.Media,
                        searchTerm: String?,
                        selectedContentType: GPHContentType
                    ) {
                        (media.imageWithRenditionType(RenditionType.fixedWidth)?.gifUrl
                            ?: media.contentUrl ?: media.source)?.let {
                            onSelection(it)
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(LocalConfiguration.current.screenHeightDp.dp)
            .then(modifier)
    )
}