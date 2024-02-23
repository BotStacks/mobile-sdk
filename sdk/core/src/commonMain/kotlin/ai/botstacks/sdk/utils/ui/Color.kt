package ai.botstacks.sdk.utils.ui

import androidx.annotation.IntRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class RgbColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    override fun toString(): String {
        return "r:$red / g:$green / b:$blue"
    }
}

data class Hsl(
    val hue: Float,
    val saturation: Float,
    val lightness: Float
)

private fun constrain(amount: Float, low: Float, high: Float): Float {
    return if (amount < low) low else if (amount > high) high else amount
}

fun rgbToHsl(
    @IntRange(from = 0x0, to = 0xFF) r: Int,
    @IntRange(from = 0x0, to = 0xFF) g: Int,
    @IntRange(from = 0x0, to = 0xFF) b: Int,
    outHsl: FloatArray
) {
    val rf = r / 255f
    val gf = g / 255f
    val bf = b / 255f

    val max = max(rf, max(gf, bf))
    val min = min(rf, min(gf, bf))
    val deltaMaxMin = max - min

    var h: Float
    val s: Float
    val l = (max + min) / 2f

    if (max == min) {
        // Monochromatic
        s = 0f
        h = s
    } else {
        h = when (max) {
            rf -> {
                (gf - bf) / deltaMaxMin % 6f
            }
            gf -> {
                (bf - rf) / deltaMaxMin + 2f
            }
            else -> {
                (rf - gf) / deltaMaxMin + 4f
            }
        }

        s = deltaMaxMin / (1f - abs(2f * l - 1f))
    }

    h = h * 60f % 360f
    if (h < 0) {
        h += 360f
    }

    outHsl[0] = constrain(h, 0f, 360f)
    outHsl[1] = constrain(s, 0f, 1f)
    outHsl[2] = constrain(l, 0f, 1f)
}

fun Color.adjustedHsl(by: Int): Color {
    val hsl = this.toHsl()
    var h2 = hsl.hue + by
    h2 = if (h2 > hsl.hue) {
        min(360f, h2)
    } else {
        max(0f, h2)
    }

    return Color.hsl(h2, hsl.saturation, hsl.lightness)
}


fun Color.rgbComponents(): RgbColor {
    with (toArgb()) {
        return RgbColor(red.toInt(), green.toInt(), blue.toInt())
    }
}

fun Color.toHsl(): Hsl {
    val rgb = rgbComponents()

    return FloatArray(3).apply {
        rgbToHsl(rgb.red, rgb.green, rgb.blue, this)
    }.let {
        Hsl(it[0], it[1], it[2])
    }
}

