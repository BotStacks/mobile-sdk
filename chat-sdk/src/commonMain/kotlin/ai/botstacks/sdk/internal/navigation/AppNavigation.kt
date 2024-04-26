package ai.botstacks.sdk.internal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

@Composable
internal expect fun BotstacksRouter(content: @Composable () -> Unit)

internal interface PlatformNavigator {
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

internal class PlatformNavigatorNull: PlatformNavigator {
    override val lastItem: Screen? = null
    override val isVisible: Boolean = false
    override val progress: Float = 0f
    override val supportsGestureNavigation: Boolean = false
    override var screensNavigator: Navigator? = null

    override fun show(screen: Screen) {

    }

    override fun hide() {
    }

    override fun push(item: Screen) {
    }

    override fun push(items: List<Screen>) {
    }

    override fun replace(item: Screen) {
    }

    override fun replaceAll(item: Screen) {
    }

    override fun replaceAll(items: List<Screen>) {
    }

    override fun pop(): Boolean = false

    override fun popAll() {
    }

    override fun popUntil(predicate: (Screen) -> Boolean): Boolean {
        return false
    }
}


internal val LocalPlatformNavigator: ProvidableCompositionLocal<PlatformNavigator> =
    staticCompositionLocalOf { PlatformNavigatorNull() }