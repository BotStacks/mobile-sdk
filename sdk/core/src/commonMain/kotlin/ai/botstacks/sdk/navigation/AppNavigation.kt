package ai.botstacks.sdk.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@Composable
internal expect fun BotstacksRouter(content: @Composable () -> Unit)

internal expect class PlatformNavigator {
    val lastItem: Screen?
    val isVisible: Boolean
    val progress: Float

    val supportsGestureNavigation: Boolean
    var screensNavigator: Navigator?

    fun show(screen: Screen)
    fun hide()
    fun push(item: Screen)

    fun push(items: List<Screen>)

    fun replace(item: Screen)

    fun replaceAll(item: Screen)

    fun replaceAll(items: List<Screen>)

    fun pop(): Boolean

    fun popAll()

    fun popUntil(predicate: (Screen) -> Boolean): Boolean
}


internal val LocalPlatformNavigator: ProvidableCompositionLocal<PlatformNavigator> =
    staticCompositionLocalOf { error("PlatformNavigator not initialized") }