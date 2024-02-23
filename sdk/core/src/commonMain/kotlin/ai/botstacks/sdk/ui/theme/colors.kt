package ai.botstacks.sdk.ui.theme

/*
 * Copyright (c) 2023.
 */

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color

val LocalBotStacksDayNightColorScheme = staticCompositionLocalOf {
    DayNightColorScheme(
        lightColors(),
        darkColors()
    )
}

val LocalBotStacksColorScheme = staticCompositionLocalOf { lightColors() }

data class DayNightColorScheme(
    val day: Colors,
    val night: Colors,
) {
    fun colors(isDark: Boolean) = if (isDark) night else day
}

@Stable
class Colors(
    isDark: Boolean,
    primary: Color,
    onPrimary: Color,
    header: Color,
    onHeader: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    border: Color,
    message: Color,
    onMessage: Color,
    chatInput: Color,
    onChatInput: Color,
    caption: Color,
    success: Color,
    onSuccess: Color,
    error: Color,
    onError: Color,
    ripple: Color,
    scrim: Color,
) {
    var isDark by mutableStateOf(isDark, structuralEqualityPolicy())
        internal set
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var onPrimary by mutableStateOf(onPrimary, structuralEqualityPolicy())
        internal set
    var header by mutableStateOf(header, structuralEqualityPolicy())
        internal set
    var onHeader by mutableStateOf(onHeader, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var onBackground by mutableStateOf(onBackground, structuralEqualityPolicy())
        internal set
    var surface by mutableStateOf(surface, structuralEqualityPolicy())
        internal set
    var onSurface by mutableStateOf(onSurface, structuralEqualityPolicy())
        internal set
    var onSurfaceVariant by mutableStateOf(onSurfaceVariant, structuralEqualityPolicy())
        internal set
    var border by mutableStateOf(border, structuralEqualityPolicy())
        internal set
    var message by mutableStateOf(message, structuralEqualityPolicy())
        internal set
    var onMessage by mutableStateOf(onMessage, structuralEqualityPolicy())
        internal set
    var chatInput by mutableStateOf(chatInput, structuralEqualityPolicy())
        internal set
    var onChatInput by mutableStateOf(onChatInput, structuralEqualityPolicy())
        internal set
    var caption by mutableStateOf(caption, structuralEqualityPolicy())
        internal set
    var success by mutableStateOf(success, structuralEqualityPolicy())
        internal set
    var onSuccess by mutableStateOf(onSuccess, structuralEqualityPolicy())
        internal set
    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var onError by mutableStateOf(onError, structuralEqualityPolicy())
        internal set

    var ripple by mutableStateOf(ripple, structuralEqualityPolicy())
        internal set

    var scrim by mutableStateOf(scrim, structuralEqualityPolicy())
        internal set

    /** Returns a copy of this ColorScheme, optionally overriding some of the values. */
    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        header: Color = this.header,
        onHeader: Color = this.onHeader,
        background: Color = this.background,
        onBackground: Color = this.onBackground,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        onSurfaceVariant: Color = this.onSurfaceVariant,
        border: Color = this.border,
        message: Color = this.message,
        onMessage: Color = this.onMessage,
        chatInput: Color = this.chatInput,
        onChatInput: Color = this.onChatInput,
        caption: Color = this.caption,
        success: Color = this.success,
        onSuccess: Color = this.onSuccess,
        error: Color = this.error,
        onError: Color = this.onError,
        ripple: Color = this.ripple,
        scrim: Color = this.scrim,
    ): Colors = Colors(
        isDark = this.isDark,
        primary = primary,
        onPrimary = onPrimary,
        header = header,
        onHeader = onHeader,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        onSurfaceVariant = onSurfaceVariant,
        border = border,
        message = message,
        onMessage = onMessage,
        chatInput = chatInput,
        onChatInput = onChatInput,
        caption = caption,
        success = success,
        onSuccess = onSuccess,
        error = error,
        onError = onError,
        ripple = ripple,
        scrim = scrim,
    )

    fun asMaterialColorScheme(): ColorScheme {
        return if (isDark) {
            darkColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                background = background,
                onBackground = onBackground,
                primaryContainer = header,
                onPrimaryContainer = onHeader,
                surface = surface,
                onSurface = onSurface,
                outline = border,
                error = error,
                onError = onError,
                scrim = scrim,
            )
        } else {
            lightColorScheme(
                primary = primary,
                onPrimary = onPrimary,
                background = background,
                onBackground = onBackground,
                primaryContainer = header,
                onPrimaryContainer = onHeader,
                surface = surface,
                onSurface = onSurface,
                outline = border,
                error = error,
                onError = onError,
                scrim = scrim,
            )
        }
    }
}

fun lightColors(
    primary: Color = BotStacksColorPalette.primary._800,
    onPrimary: Color = BotStacksColorPalette.light._900,
    header: Color = BotStacksColorPalette.primary._100,
    onHeader: Color = BotStacksColorPalette.dark._900,
    background: Color = BotStacksColorPalette.light._900,
    onBackground: Color = BotStacksColorPalette.dark._600,
    surface: Color = BotStacksColorPalette.light._500,
    onSurface: Color = BotStacksColorPalette.dark._900,
    onSurfaceVariant: Color = BotStacksColorPalette.light._100,
    border: Color = BotStacksColorPalette.light._500,
    message: Color = BotStacksColorPalette.light._700,
    onMessage: Color = BotStacksColorPalette.dark._900,
    chatInput: Color = BotStacksColorPalette.light._600,
    onChatInput: Color = BotStacksColorPalette.dark._900,
    caption: Color = BotStacksColorPalette.dark._100,
    success: Color = BotStacksColorPalette.green._800,
    onSuccess: Color = BotStacksColorPalette.dark._400,
    error: Color = BotStacksColorPalette.red._800,
    onError: Color = BotStacksColorPalette.light._900,
    ripple: Color = BotStacksColorPalette.dark._100,
    scrim: Color = Color(0x30313A3B)
) = Colors(
    isDark = false,
    primary = primary,
    onPrimary = onPrimary,
    header = header,
    onHeader = onHeader,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    onSurfaceVariant = onSurfaceVariant,
    border = border,
    message = message,
    onMessage = onMessage,
    chatInput = chatInput,
    onChatInput = onChatInput,
    caption = caption,
    success = success,
    onSuccess = onSuccess,
    error = error,
    onError = onError,
    ripple = ripple,
    scrim = scrim,
)

fun darkColors(
    primary: Color = BotStacksColorPalette.primary._700,
    onPrimary: Color = BotStacksColorPalette.light._900,
    header: Color = BotStacksColorPalette.dark._900,
    onHeader: Color = BotStacksColorPalette.light._900,
    background: Color = BotStacksColorPalette.dark._800,
    onBackground: Color = BotStacksColorPalette.light._600,
    surface: Color = BotStacksColorPalette.dark._900,
    onSurface: Color = BotStacksColorPalette.light._900,
    onSurfaceVariant: Color = BotStacksColorPalette.dark._100,
    border: Color = BotStacksColorPalette.dark._400,
    message: Color = BotStacksColorPalette.dark._500,
    onMessage: Color = BotStacksColorPalette.light._900,
    chatInput: Color = BotStacksColorPalette.dark._500,
    onChatInput: Color = BotStacksColorPalette.light._600,
    caption: Color = BotStacksColorPalette.dark._100,
    success: Color = BotStacksColorPalette.green._700,
    onSuccess: Color = BotStacksColorPalette.dark._400,
    error: Color = BotStacksColorPalette.red._700,
    onError: Color = Color(0xFF29292D),
    ripple: Color = BotStacksColorPalette.light._500,
    scrim: Color = Color(0x4B4D5866)
) = Colors(
    isDark = true,
    primary = primary,
    onPrimary = onPrimary,
    header = header,
    onHeader = onHeader,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    onSurfaceVariant = onSurfaceVariant,
    border = border,
    message = message,
    onMessage = onMessage,
    chatInput = chatInput,
    onChatInput = onChatInput,
    caption = caption,
    success = success,
    onSuccess = onSuccess,
    error = error,
    onError = onError,
    ripple = ripple,
    scrim = scrim,
)

@Composable
fun dayNightColor(day: Color, night: Color) = if (isSystemInDarkTheme()) night else day

val Colors.dialogCancelBackground: Color
    @Composable get() {
        val palette = LocalBotStacksColorPalette.current
        return dayNightColor(palette.dark._900, palette.dark._400)
    }