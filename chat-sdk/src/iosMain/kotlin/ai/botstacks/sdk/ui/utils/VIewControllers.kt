package ai.botstacks.sdk.ui.utils

import ai.botstacks.sdk.internal.utils.ui.addIf
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.components.HeaderHeight
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.useContents
import platform.UIKit.UIView
import platform.UIKit.UIViewController

@OptIn(ExperimentalComposeApi::class)
internal fun measuredThemedViewController(
    onMeasured: (Double, Double) -> Unit,
    content: @Composable () -> Unit,
): UIViewController = ComposeUIViewController(
    configure = {
        opaque = true
    }
) {
    with(BotStacksTheme) {
        BotStacksThemeEngine(
            useDarkTheme = useDarkMode,
            lightColorScheme = lightColors,
            darkColorScheme = darkColors,
            shapes = shapes,
            assets = assets,
            fonts = fonts,
        ) {
            Box(
                modifier = Modifier.onSizeChanged {
                    onMeasured(it.width.toDouble(), it.height.toDouble())
                },
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun IntrinsicWidthUIKitView(uiView: UIView) {
    var width by remember(uiView) { mutableStateOf(0.dp) }
    var updateCount by remember(uiView) { mutableStateOf(0) }

    val density = LocalDensity.current
    UIKitView(
        background = BotStacks.colorScheme.header,
        factory = { uiView },
        onResize = { view, size ->
            size.useContents contents@{
                if (updateCount < 1) {
                    width = with(density) { this@contents.size.width.toInt().toDp() }
                    updateCount++
                }
            }
            view.setFrame(size)
        },
        modifier = Modifier
            .addIf(width > 0.dp) { Modifier.width(width) }
            .height(HeaderHeight)
    )
}