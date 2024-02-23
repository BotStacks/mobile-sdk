package ai.botstacks.sdk.ui.theme

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalBotStacksDimens = staticCompositionLocalOf<Dimensions> { error("No Dimensions provided") }


private val staticGridPreset =
    GridDimensionSet(
        x1 = 4.dp,
        x2 = 8.dp,
        x3 = 12.dp,
        x4 = 16.dp,
        x5 = 20.dp,
        x6 = 24.dp,
        x7 = 28.dp,
        x8 = 32.dp,
        x9 = 36.dp,
        x10 = 40.dp,
        x11 = 44.dp,
        x12 = 48.dp,
        x13 = 52.dp,
        x14 = 56.dp,
        x15 = 60.dp,
        x16 = 64.dp,
    )

class Dimensions(
    val none: Dp = 0.dp,
    val border: Dp = 1.dp,
    val thickBorder: Dp = 2.dp,
    val inset: Dp,
    val screenWidth: Dp = Dp.Unspecified,
    val screenHeight: Dp = Dp.Unspecified,
    val widthWindowSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    val heightWindowSizeClass: WindowHeightSizeClass = WindowHeightSizeClass.Compact,
    val imagePreviewSize: Size = Size(width = 178f, height = 152f),
    val videoPreviewSize: Size = Size(width = 178f, height = 152f),
    /**
     * Material design has grid spacings by 4dp increments for normal use cases
     * This field is dynamically sized based on screen size
     */
    val grid: GridDimensionSet,
    /**
     * A static grid that is screen size independent based on Material design 4dp spacing
     */
    val staticGrid: GridDimensionSet = staticGridPreset,
) {
    val isMediumWidth: Boolean
        get() = widthWindowSizeClass == WindowWidthSizeClass.Medium

    val isLargeWidth: Boolean
        get() = widthWindowSizeClass == WindowWidthSizeClass.Expanded
}

data class GridDimensionSet(
    val x1: Dp,
    val x2: Dp,
    val x3: Dp,
    val x4: Dp,
    val x5: Dp,
    val x6: Dp,
    val x7: Dp,
    val x8: Dp,
    val x9: Dp,
    val x10: Dp,
    val x11: Dp,
    val x12: Dp,
    val x13: Dp,
    val x14: Dp,
    val x15: Dp,
    val x16: Dp,
)
