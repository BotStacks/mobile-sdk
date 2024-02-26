package ai.botstacks.sdk

import ai.botstacks.sdk.internal.navigation.BotstacksRouter
import ai.botstacks.sdk.internal.navigation.LocalPlatformNavigator
import ai.botstacks.sdk.internal.navigation.screens.ChatListScreen
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.Assets
import ai.botstacks.sdk.ui.theme.Colors
import ai.botstacks.sdk.ui.theme.Fonts
import ai.botstacks.sdk.ui.theme.darkBotStacksColors
import ai.botstacks.sdk.ui.theme.lightBotStacksColors
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Shapes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlin.experimental.ExperimentalObjCName


@OptIn(ExperimentalObjCName::class)
@ObjCName("BotStacksChatController")
fun BotStacksChatController(onLogout: () -> Unit) = BotStacksChatController(
    useDarkTheme = false,
    lightColorScheme = null,
    darkColorScheme = null,
    shapes = null,
    assets = null,
    fonts = null,
    onLogout = onLogout
)

@OptIn(ExperimentalObjCName::class)
@ObjCName("BotStacksChatController")
fun BotStacksChatController(
    useDarkTheme: Boolean = false,
    lightColorScheme: Colors? = null,
    darkColorScheme: Colors? = null,
    shapes: Shapes? = null,
    assets: Assets? = null,
    fonts: Fonts? = null,
    onLogout: () -> Unit
) = ComposeUIViewController {
    BotStacksChat.shared.onLogout = onLogout

    BotStacksThemeEngine(
        useDarkTheme,
        lightColorScheme ?: lightBotStacksColors(),
        darkColorScheme ?: darkBotStacksColors(),
        shapes ?: BotStacks.shapes,
        assets ?: BotStacks.assets,
        fonts,
    ) {
        BotstacksRouter {
            Navigator(ChatListScreen) { navigator ->
                val platformNavigator = LocalPlatformNavigator.current
                LaunchedEffect(navigator.lastItem) {
                    // update global navigator for platform access to support push/pop from a single
                    // navigator current
                    platformNavigator.screensNavigator = navigator
                }

                Box(modifier = Modifier) {
                    SlideTransition(navigator)
                }
            }
        }
    }
}