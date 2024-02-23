@file:OptIn(ExperimentalResourceApi::class)

package ai.botstacks.sdk.ui.resources

import ai.botstacks.sdk.ui.theme.LocalBotStacksColorScheme
import androidx.compose.runtime.Composable
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi


internal val Res.drawable.botstacks_logo_daynight: DrawableResource
    @Composable get() = if (LocalBotStacksColorScheme.current.isDark) botstacks_logo_dark else botstacks_logo