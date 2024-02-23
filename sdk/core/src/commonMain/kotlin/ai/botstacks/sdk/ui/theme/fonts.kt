@file:OptIn(ExperimentalResourceApi::class)

package ai.botstacks.sdk.ui.theme

/*
 * Copyright (c) 2023.
 */

import ai.botstacks.sdk.utils.ifteq
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

internal val LocalBotStacksFonts = staticCompositionLocalOf {
    Fonts(
        h1 = FontStyle(),
        h2 = FontStyle(),
        h3 = FontStyle(),
        body1 = FontStyle(),
        body2 = FontStyle(),
        label1 = FontStyle(),
        label2 = FontStyle(),
        button1 = FontStyle(),
        button2 = FontStyle(),
        caption1 = FontStyle(),
        caption2 = FontStyle(),
    )
}

@Stable
data class FontStyle(
    val size: TextUnit = 14.sp,
    val weight: FontWeight = FontWeight.Normal,
    val family: FontFamily = FontFamily.Default,
    val textAlign: TextAlign = TextAlign.Unspecified,
) {
    @Composable
    fun asTextStyle(): TextStyle {
        return TextStyle(
            fontFamily = family,
            fontSize = size,
            fontWeight = weight,
            textAlign = textAlign,
        )
    }
}

@Stable
class Fonts(
    val h1: FontStyle,
    val h2: FontStyle,
    val h3: FontStyle,
    val body1: FontStyle,
    val body2: FontStyle,
    val label1: FontStyle,
    val label2: FontStyle,
    val button1: FontStyle,
    val button2: FontStyle,
    val caption1: FontStyle,
    val caption2: FontStyle,
//    val title: FontStyle = FontStyle(size = 22.sp),
//    val title2: FontStyle = FontStyle(size = 17.sp),
//    val title3: FontStyle = FontStyle(size = 15.sp),
//    val headline: FontStyle = FontStyle(size = 13.sp, weight = FontWeight.Bold),
//    val body: FontStyle = FontStyle(size = 13.sp),
//    val caption: FontStyle = FontStyle(size = 10.sp),
//    val username: FontStyle = FontStyle(size = 12.sp, weight = FontWeight.ExtraBold),
//    val timestamp: FontStyle = FontStyle(size = 12.sp),
//    val mini: FontStyle = FontStyle(size = 10.sp)
) {
    @Stable
    fun merge(other: Fonts? = null): Fonts {
        if (other == null) return this

        return Fonts(
            h1 = ifteq(h1, other.h1),
            h2 = ifteq(h2, other.h2),
            h3 = ifteq(h3, other.h3),
            body1 = ifteq(body1, other.body1),
            body2 = ifteq(body2, other.body2),
            label1 = ifteq(label1, other.label1),
            label2 = ifteq(label2, other.label2),
            button1 = ifteq(button1, other.button1),
            button2 = ifteq(button2, other.button2),
            caption1 = ifteq(caption1, other.caption1),
            caption2 = ifteq(caption2, other.caption2),
        )
    }

    fun copy(
        h1: FontStyle = this.h1,
        h2: FontStyle = this.h2,
        h3: FontStyle = this.h3,
        body1: FontStyle = this.body1,
        body2: FontStyle = this.body2,
        label1: FontStyle = this.label1,
        label2: FontStyle = this.label2,
        button1: FontStyle = this.button1,
        button2: FontStyle = this.button2,
        caption1: FontStyle = this.caption1,
        caption2: FontStyle = this.caption2,
    ): Fonts = Fonts(
        h1 = h1,
        h2 = h2,
        h3 = h3,
        body1 = body1,
        body2 = body2,
        label1 = label1,
        label2 = label2,
        button1 = button1,
        button2 = button2,
        caption1 = caption1,
        caption2 = caption2
    )
}

@Composable
fun fonts(
    h1: FontStyle = defaultAppFonts().h1,
    h2: FontStyle = defaultAppFonts().h2,
    h3: FontStyle = defaultAppFonts().h3,
    body1: FontStyle = defaultAppFonts().body1,
    body2: FontStyle = defaultAppFonts().body2,
    label1: FontStyle = defaultAppFonts().label1,
    label2: FontStyle = defaultAppFonts().label2,
    button1: FontStyle = defaultAppFonts().button1,
    button2: FontStyle = defaultAppFonts().button2,
    caption1: FontStyle = defaultAppFonts().caption1,
    caption2: FontStyle = defaultAppFonts().caption2,
) = Fonts(
    h1 = h1,
    h2 = h2,
    h3 = h3,
    body1 = body1,
    body2 = body2,
    label1 = label1,
    label2 = label2,
    button1 = button1,
    button2 = button2,
    caption1 = caption1,
    caption2 = caption2
)

@Composable
private fun defaultAppFonts() = Fonts(
    h1 = FontStyle(
        size = 23.sp,
        weight = FontWeight.W700,
        family = interW700
    ),
    h2 = FontStyle(
        size = 19.sp,
        weight = FontWeight.W600,
        family = interW600
    ),
    h3 = FontStyle(
        size = 15.sp,
        weight = FontWeight.W700,
        family = interW700
    ),
    body1 = FontStyle(
        size = 15.sp,
        weight = FontWeight.W400,
        family = interW400
    ),
    body2 = FontStyle(
        size = 14.sp,
        weight = FontWeight.W400,
        family = interW400
    ),
    label1 = FontStyle(
        size = 15.sp,
        weight = FontWeight.W500,
        family = interW500
    ),
    label2 = FontStyle(
        size = 14.sp,
        weight = FontWeight.W500,
        family = interW600
    ),
    button1 = FontStyle(
        size = 15.sp,
        weight = FontWeight.W500,
        family = interW500
    ),
    button2 = FontStyle(
        size = 14.sp,
        weight = FontWeight.W500,
        family = interW500
    ),
    caption1 = FontStyle(
        size = 13.sp,
        weight = FontWeight.W400,
        family = interW400
    ),
    caption2 = FontStyle(
        size = 12.sp,
        weight = FontWeight.W400,
        family = interW400
    ),
)

private val interW400: FontFamily
    @Composable get() = FontFamily(Font(Res.font.inter_w400))

private val interW500: FontFamily
    @Composable get() = FontFamily(Font(Res.font.inter_w500))

private val interW600: FontFamily
    @Composable get() = FontFamily(Font(Res.font.inter_w600))

private val interW700: FontFamily
    @Composable get() = FontFamily(Font(Res.font.inter_w700))