package ai.botstacks.sdk.internal.utils.ui

import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreGraphics.CGFloatVar
import platform.UIKit.UIColor
import kotlin.math.roundToInt

internal val Color.ui : UIColor
    get() = UIColor(
        alpha = alpha.toDouble(),
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble()
    )

val UIColor.composeColor: Color
    get() = memScoped {
        val red = alloc<CGFloatVar>()
        val green = alloc<CGFloatVar>()
        val blue = alloc<CGFloatVar>()
        val alpha = alloc<CGFloatVar>()

        getRed(
            red = red.ptr,
            green = green.ptr,
            blue = blue.ptr,
            alpha = alpha.ptr
        )

        Color(
            alpha = alpha.value.toFloat().coerceIn(0f,1f),
            red = red.value.toFloat().coerceIn(0f,1f),
            green = green.value.toFloat().coerceIn(0f,1f),
            blue = blue.value.toFloat().coerceIn(0f,1f),
        )
    }