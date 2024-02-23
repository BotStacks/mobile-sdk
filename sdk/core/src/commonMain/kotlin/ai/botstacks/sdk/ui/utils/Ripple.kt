package ai.botstacks.sdk.ui.utils

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
object FullAlphaRipple : RippleTheme {
    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        BotStacks.colorScheme.ripple,
        !BotStacks.colorScheme.isDark
    )

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        BotStacks.colorScheme.ripple,
        !BotStacks.colorScheme.isDark
    )
}