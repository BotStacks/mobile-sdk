package ai.botstacks.sdk.internal.navigation

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.ui.components.ContextMenuContainer
import ai.botstacks.sdk.internal.utils.Platform
import ai.botstacks.sdk.internal.utils.shouldUseSwipeBack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.compositionUniqueId

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal actual fun BotstacksRouter(content: @Composable () -> Unit) {
    ContextMenuContainer {
        BackdropNavigator(
            modifier = Modifier.fillMaxSize(),
            sheetBackgroundColor = BotStacks.colorScheme.background,
            sheetContentColor = BotStacks.colorScheme.onBackground,
            sheetShape = BotStacks.shapes.large,
            sheetContent = {
                val navigator = remember(it.key) { iOSPlatformNavigator(it) }
                CompositionLocalProvider(LocalPlatformNavigator provides navigator) {
                    CurrentScreen()
                }
            }
        ) {
            val navigator = remember(it.key) { iOSPlatformNavigator(it) }
            CompositionLocalProvider(LocalPlatformNavigator provides navigator) {
                content()
            }
        }
    }
}

internal class iOSPlatformNavigator(
    private val navigator: BackdropNavigator,
) : PlatformNavigator {

    init {
        println("Creating instance of iOS navigator")
    }

    val key: String
        get() = navigator.key

    override val lastItem: Screen?
        get() = navigator.lastItemOrNull

    override val isVisible: Boolean
        get() = navigator.isOpen

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
        println("pop :: ${screensNavigator != null}")
        return screensNavigator?.pop() ?: navigator.pop()
    }

    override fun popAll() {
        screensNavigator?.popAll()
    }

    override fun popUntil(predicate: (Screen) -> Boolean): Boolean {
        return screensNavigator?.popUntil(predicate) ?: false
    }
}