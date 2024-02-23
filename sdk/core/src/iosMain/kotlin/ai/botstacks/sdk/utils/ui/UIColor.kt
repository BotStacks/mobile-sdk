package ai.botstacks.sdk.utils.ui

import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColor

internal val Color.ui : UIColor
    get() = UIColor(
        alpha = alpha.toDouble(),
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble()
    )