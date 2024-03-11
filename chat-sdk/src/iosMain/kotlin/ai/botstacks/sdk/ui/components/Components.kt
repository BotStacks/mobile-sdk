package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun _Badge(
    count: Int,
    onMeasured: (Double, Double) -> Unit,
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
            Badge(
                modifier = Modifier.onGloballyPositioned {
                    println("onMeasured = ${it.size}")
                    onMeasured(it.size.width.toDouble(), it.size.height.toDouble())
                },
                count = count
            )
        }
    }
}