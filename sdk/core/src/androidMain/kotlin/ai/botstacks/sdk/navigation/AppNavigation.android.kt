package ai.botstacks.sdk.navigation

import ai.botstacks.sdk.utils.Platform
import ai.botstacks.sdk.utils.shouldUseSwipeBack
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal actual fun BotstacksRouter(content: @Composable () -> Unit) {
    BottomSheetNavigator(
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            val navigator = remember(it) { PlatformNavigator(it) }
            CompositionLocalProvider(LocalPlatformNavigator provides navigator) {
                CurrentScreen()
            }
        }
    ) {
        val navigator = remember(it) { PlatformNavigator(it) }
        CompositionLocalProvider(LocalPlatformNavigator provides navigator) {
            content()
        }
    }

    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()
    SideEffect {
        val window = (view.context as Activity).window
        window.navigationBarColor =  Color(0x01000000).toArgb()
        window.statusBarColor = Color(0x01000000).toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }
}

internal actual class PlatformNavigator(
    private val navigator: BottomSheetNavigator
) {

    actual val lastItem: Screen?
        get() = navigator.lastItemOrNull

    actual val isVisible: Boolean
        get() = navigator.isVisible

    actual val progress: Float
        get() = navigator.progress

    actual var screensNavigator: Navigator? = null

    actual val supportsGestureNavigation: Boolean
        get() = Platform.shouldUseSwipeBack

    actual fun show(screen: Screen) {
        navigator.show(screen)
    }

    actual fun hide() {
        navigator.hide()
    }

    actual fun push(item: Screen) {
        screensNavigator?.push(item)
    }

    actual fun push(items: List<Screen>) {
        screensNavigator?.push(items)
    }

    actual fun replace(item: Screen) {
        screensNavigator?.replace(item)
    }

    actual fun replaceAll(item: Screen) {
        screensNavigator?.replaceAll(item)
    }

    actual fun replaceAll(items: List<Screen>) {
        screensNavigator?.replaceAll(items)
    }

    actual fun pop(): Boolean {
        return screensNavigator?.pop() ?: false
    }

    actual fun popAll() {
        screensNavigator?.popAll()
    }

    actual fun popUntil(predicate: (Screen) -> Boolean): Boolean {
        return screensNavigator?.popUntil(predicate) ?: false
    }
}