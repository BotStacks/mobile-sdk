package ai.botstacks.sdk.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal object BotStacksColorPalette {
    val primary = BluePalette
    val green = GreenPalette
    val red = RedPalette
    val yellow = YellowPalette
    val dark = DarkPalette
    val light = LightPalette

    val dayNight: ColorPalette
        @Composable get() = if (LocalBotStacksColorScheme.current.isDark) dark else light
}

internal val LocalBotStacksColorPalette = staticCompositionLocalOf { BotStacksColorPalette }

internal sealed interface ColorPalette {
    val _100: Color
    val _200: Color
    val _300: Color
    val _400: Color
    val _500: Color
    val _600: Color
    val _700: Color
    val _800: Color
    val _900: Color

}
internal data object BluePalette : ColorPalette {
    override val _100 = Color(0xFFF6F7FF)
    override val _200 = Color(0xFFEBEDFF)
    override val _300 = Color(0xFFD9DDFF)
    override val _400 = Color(0xFFB2C4FF)
    override val _500 = Color(0xFF89A4FF)
    override val _600 = Color(0xFF7487FF)
    override val _700 = Color(0xFF4772FF)
    override val _800 = Color(0xFF295BFF)
    override val _900 = Color(0xFF0E3FDF)
}

internal data object GreenPalette : ColorPalette {
    override val _100 = Color(0xFFDBFDE9)
    override val _200 = Color(0xFFECFEF3)
    override val _300 = Color(0xFFDBFDE9)
    override val _400 = Color(0xFFB7FAD2)
    override val _500 = Color(0xFF90F8BA)
    override val _600 = Color(0xFF7CF7AD)
    override val _700 = Color(0xFF52F493)
    override val _800 = Color(0xFF36F281)
    override val _900 = Color(0xFF0CC054)
}

internal data object RedPalette : ColorPalette {
    override val _100 = Color(0xFFFFDCD9)
    override val _200 = Color(0xFFFFECEB)
    override val _300 = Color(0xFFFFDCD9)
    override val _400 = Color(0xFFFFB7B2)
    override val _500 = Color(0xFFFF9189)
    override val _600 = Color(0xFFFF7D74)
    override val _700 = Color(0xFFFF5347)
    override val _800 = Color(0xFFFF3729)
    override val _900 = Color(0xFFDF1B0E)
}

internal data object YellowPalette : ColorPalette {
    override val _100 = Color(0xFFFFFBEB)
    override val _200 = Color(0xFFFFF5D1)
    override val _300 = Color(0xFFFFF0BC)
    override val _400 = Color(0xFFFFEA9F)
    override val _500 = Color(0xFFFFE589)
    override val _600 = Color(0xFFF4D66A)
    override val _700 = Color(0xFFECC94A)
    override val _800 = Color(0xFFDDB730)
    override val _900 = Color(0xFFAD8800)
}

internal data object DarkPalette : ColorPalette {
    override val _100 = Color(0xFF87878C)
    override val _200 = Color(0xFF71717A)
    override val _300 = Color(0xFF62626A)
    override val _400 = Color(0xFF53535A)
    override val _500 = Color(0xFF45454A)
    override val _600 = Color(0xFF3B3B3F)
    override val _700 = Color(0xFF36363A)
    override val _800 = Color(0xFF313135)
    override val _900 = Color(0xFF2B2B2F)
}

internal data object LightPalette : ColorPalette {
    override val _100 = Color(0xFFCFCFD3)
    override val _200 = Color(0xFFD5D5D8)
    override val _300 = Color(0xFFDADADD)
    override val _400 = Color(0xFFE4E4E7)
    override val _500 = Color(0xFFEAEAEB)
    override val _600 = Color(0xFFEFEFF0)
    override val _700 = Color(0xFFF4F4F5)
    override val _800 = Color(0xFFFAFAFA)
    override val _900 = Color(0xFFFFFFFF)
}