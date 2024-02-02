package ai.botstacks.sdk.navigation

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.navigation.screens.ChatListScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

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

@Composable
fun BotstacksNavigation(onLogout: () -> Unit) {
    BotStacksChat.shared.onLogout = onLogout

    BotstacksRouter {
        Navigator(ChatListScreen) { navigator ->
            val platformNavigator = LocalPlatformNavigator.current
            LaunchedEffect(navigator.lastItem) {
                // update global navigator for platform access to support push/pop from a single
                // navigator current
                platformNavigator.screensNavigator = navigator
            }


            SharedAppScaffolding(navigator) {
                // TODO support swipe back on iOS
                SlideTransition(navigator)
            }
        }
    }
}

@Composable
private fun SharedAppScaffolding(
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    Scaffold(
        content = {
            Box(modifier = Modifier) {
                content()
            }
        }
    )
}