package ai.botstacks.sdk.ui.utils

import ai.botstacks.sdk.internal.utils.ui.addIf
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGSizeZero
import platform.UIKit.UIView
import platform.UIKit.UIViewController
import platform.darwin.NSObject

internal fun measuredThemedViewController(
    onMeasured: (Double, Double) -> Unit,
    content: @Composable () -> Unit,
): UIViewController = ComposeUIViewController {
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
                modifier = Modifier
                    .onSizeChanged {
                        onMeasured(it.width.toDouble(), it.height.toDouble())
                    },
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun <T: UIView> IntrinsicUIKitView(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    widthIn: Boolean = true,
    heightIn: Boolean = false,
    uiView: () -> T,
    update: (T) -> Unit = { },
) {
    val view = remember(uiView) { uiView() }
    val size = remember(view) {
        view.sizeThatFits(cValue { CGSizeZero })
            .useContents { DpSize(width.dp, height.dp) }
    }

    Box {
        UIKitView(
            background = backgroundColor,
            factory = { view },
            update = update,
            modifier = Modifier
                .addIf(widthIn) { Modifier.width(size.width) }
                .addIf(heightIn) { Modifier.height(size.height) }
                .then(modifier)
        )
    }
}