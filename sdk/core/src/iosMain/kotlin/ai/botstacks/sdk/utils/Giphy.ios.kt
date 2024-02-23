package ai.botstacks.sdk.utils


import ai.botstacks.sdk.ui.rootViewController
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cocoapods.Giphy.GPHContentType
import cocoapods.Giphy.GPHContentTypeGifs
import cocoapods.Giphy.GPHFileExtensionGif
import cocoapods.Giphy.GPHMedia
import cocoapods.Giphy.GPHRenditionTypeFixedWidth
import cocoapods.Giphy.GPHStickerColumnCountFour
import cocoapods.Giphy.GPHStickerColumnCountThree
import cocoapods.Giphy.GPHStickerColumnCountTwo
import cocoapods.Giphy.GPHTheme
import cocoapods.Giphy.GPHThemeTypeAutomatic
import cocoapods.Giphy.GPHThemeTypeDark
import cocoapods.Giphy.GPHThemeTypeLight
import cocoapods.Giphy.Giphy
import cocoapods.Giphy.GiphyDelegateProtocol
import cocoapods.Giphy.GiphyViewController
import cocoapods.Giphy.urlWithRendition
import platform.UIKit.UIApplication
import platform.darwin.NSObject

actual typealias Giphy = Giphy
actual typealias GiphyModal = GiphyViewController
actual typealias GiphyThemeType = Int
actual typealias GiphyContentType = Int

actual fun GiphyModal.setStickerColumnCount(@IntRange(from = 2, to = 4) count: Int) {
    val columnCount = when (count) {
        2 -> GPHStickerColumnCountTwo
        3 -> GPHStickerColumnCountThree
        4 -> GPHStickerColumnCountFour
        else -> null
    }
    if (columnCount != null) {
        setStickerColumnCount(columnCount)
    }
}

actual fun GiphyModal.setTheme(themeType: GiphyThemeType) {
    setTheme(GPHTheme().apply {
        setType(
            when (themeType) {
                0 -> GPHThemeTypeAutomatic
                1 -> GPHThemeTypeLight
                else -> GPHThemeTypeDark
            }
        )
    })
}

actual fun GiphyModal.setMediaType(@IntRange(from = 0, to = 5) contentType: GiphyContentType) {
    setMediaConfigWithTypes(listOf(contentType))
}

@Composable
actual fun GiphyModalSheet(
    modifier: Modifier,
    onCancel: () -> Unit,
    onSelection: (String) -> Unit
) {
    val giphy = GiphyViewController().apply {
        this.setMediaConfigWithTypes(listOf(GPHContentTypeGifs))
        this.setStickerColumnCount(GPHStickerColumnCountThree)
        this.setTheme(GPHTheme().apply { setType(GPHThemeTypeAutomatic) })

        this.setDelegate(object : NSObject(), GiphyDelegateProtocol {
            override fun didSelectMediaWithGiphyViewController(
                giphyViewController: GiphyViewController,
                media: GPHMedia,
                contentType: GPHContentType
            ) {
                val url = media.urlWithRendition(
                    GPHRenditionTypeFixedWidth,
                    GPHFileExtensionGif
                ) ?: media.contentUrl() ?: media.source()

                if (url != null) {
                    onSelection(url)
                    giphyViewController.dismissViewControllerAnimated(true, null)
                }
            }
            override fun didDismissWithController(controller: GiphyViewController?) {
                onCancel()
            }
        })
    }

    UIApplication.rootViewController()?.presentViewController(giphy, animated = true, completion = null)
}