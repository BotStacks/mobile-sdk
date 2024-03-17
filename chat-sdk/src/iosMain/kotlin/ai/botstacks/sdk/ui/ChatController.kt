package ai.botstacks.sdk.ui

import ai.botstacks.sdk.BotStacksChatController
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController

@OptIn(ExperimentalComposeApi::class)
fun _ChatController(onLogout: () -> Unit) = ComposeUIViewController(
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
            BotStacksChatController(onLogout)
        }
    }
}