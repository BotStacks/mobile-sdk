package ai.botstacks.sdk.internal.navigation

import ai.botstacks.sdk.internal.utils.Platform
import ai.botstacks.sdk.internal.utils.shouldUseSwipeBack
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
            val navigator = remember(it) { AndroidPlatformNavigator(it) }
            CompositionLocalProvider(LocalPlatformNavigator provides navigator) {
                CurrentScreen()
            }
        }
    ) {
        val navigator = remember(it) { AndroidPlatformNavigator(it) }
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

internal class AndroidPlatformNavigator(
    private val navigator: BottomSheetNavigator
) : PlatformNavigator {

    override val lastItem: Screen?
        get() = navigator.lastItemOrNull

    override val isVisible: Boolean
        get() = navigator.isVisible

    override val progress: Float
        get() = navigator.progress

    override var screensNavigator: Navigator? = null

    override val supportsGestureNavigation: Boolean
        get() = Platform.shouldUseSwipeBack

    override fun show(screen: Screen) {
        navigator.show(screen)
    }

    override fun hide() {
        navigator.hide()
    }

    override fun push(item: Screen) {
        screensNavigator?.push(item)
    }

    override fun push(items: List<Screen>) {
        screensNavigator?.push(items)
    }

    override fun replace(item: Screen) {
        screensNavigator?.replace(item)
    }

    override fun replaceAll(item: Screen) {
        screensNavigator?.replaceAll(item)
    }

    override fun replaceAll(items: List<Screen>) {
        screensNavigator?.replaceAll(items)
    }

    override fun pop(): Boolean {
        return screensNavigator?.pop() ?: false
    }

    override fun popAll() {
        screensNavigator?.popAll()
    }

    override fun popUntil(predicate: (Screen) -> Boolean): Boolean {
        return screensNavigator?.popUntil(predicate) ?: false
    }
}