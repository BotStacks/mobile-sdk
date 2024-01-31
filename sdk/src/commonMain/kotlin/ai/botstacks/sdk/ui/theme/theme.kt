package ai.botstacks.sdk.ui.theme
/*
 * Copyright (c) 2023.
 */

import ai.botstacks.sdk.ui.utils.FullAlphaRipple
import ai.botstacks.sdk.utils.ui.calculateScreenSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yazantarifi.compose.library.MarkdownConfig

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
internal fun Theme(
    assets: Assets,
    isDark: Boolean,
    colorScheme: DayNightColorScheme,
    fonts: Fonts,
    shapes: Shapes,
    content: @Composable () -> Unit,
) {
    val _colorsScheme = remember(isDark, colorScheme) {
        colorScheme.colors(isDark)
    }

    @Stable
    fun markdownConfig(sender: Boolean) = MarkdownConfig(
        isLinksClickable = true,
        isImagesClickable = false,
        isScrollEnabled = false,
        colors = HashMap<String, Color>().apply {
            this[MarkdownConfig.CHECKBOX_COLOR] = Color.Black
            this[MarkdownConfig.LINKS_COLOR] = _colorsScheme.primary
            this[MarkdownConfig.TEXT_COLOR] = if (sender) _colorsScheme.onPrimary else _colorsScheme.onMessage
            this[MarkdownConfig.HASH_TEXT_COLOR] = _colorsScheme.primary
            this[MarkdownConfig.CODE_BACKGROUND_COLOR] = Color.Gray
            this[MarkdownConfig.CODE_BLOCK_TEXT_COLOR] = Color.White
        }
    )

    val screenSize = calculateScreenSize()
    val windowSizeClass = calculateWindowSizeClass()
    val dimens = Dimensions(
        screenWidth = screenSize.width,
        screenHeight = screenSize.height,
        inset = when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Expanded -> 30.dp
            else -> 20.dp
        },
        grid = when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Expanded -> GridDimensionSet(
                8.dp,
                16.dp,
                24.dp,
                32.dp,
                40.dp,
                48.dp,
                56.dp,
                64.dp,
                72.dp,
                80.dp,
                88.dp,
                96.dp,
                104.dp,
                112.dp,
                120.dp,
                124.dp,
            )
            else -> GridDimensionSet(
                4.dp,
                8.dp,
                12.dp,
                16.dp,
                20.dp,
                24.dp,
                28.dp,
                32.dp,
                36.dp,
                40.dp,
                44.dp,
                48.dp,
                52.dp,
                56.dp,
                60.dp,
                64.dp,
            )
        },
        widthWindowSizeClass = windowSizeClass.widthSizeClass,
        heightWindowSizeClass = windowSizeClass.heightSizeClass,
    )

    CompositionLocalProvider(
        LocalBotStacksAssets provides assets,
        LocalBotStacksDayNightColorScheme provides colorScheme,
        LocalBotStacksColorScheme provides _colorsScheme,
        LocalBotStacksDimens provides dimens,
        LocalBotStacksFonts provides fonts,
        LocalBotStacksMarkdownConfig provides { sender -> markdownConfig(sender) },
        LocalBotStacksShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = _colorsScheme.asMaterialColorScheme(),
            shapes = shapes,
        ) {
            CompositionLocalProvider(
                LocalRippleTheme provides FullAlphaRipple
            ) {
                Box(
                    modifier = Modifier
                        .background(colorScheme.colors(isDark).background),
                ) {
                    CompositionLocalProvider(LocalContentColor provides colorScheme.colors(isDark).onBackground) {
                        content()
                    }
                }
            }
        }
    }
}

internal val LocalBotStacksMarkdownConfig = staticCompositionLocalOf { { sender: Boolean -> MarkdownConfig() } }
