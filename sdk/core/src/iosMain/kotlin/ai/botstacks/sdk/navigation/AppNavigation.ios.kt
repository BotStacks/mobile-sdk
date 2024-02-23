package ai.botstacks.sdk.navigation

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.internal.ContextMenuContainer
import ai.botstacks.sdk.utils.Platform
import ai.botstacks.sdk.utils.shouldUseSwipeBack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

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
    }
}

internal actual class PlatformNavigator(
    private val navigator: BackdropNavigator
) {

    actual val lastItem: Screen?
        get() = navigator.lastItemOrNull

    actual val isVisible: Boolean
        get() = navigator.isOpen

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