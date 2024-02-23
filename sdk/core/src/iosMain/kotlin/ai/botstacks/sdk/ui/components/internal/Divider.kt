package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
@NonRestartableComposable
internal fun CupertinoDivider(
    modifier: Modifier = Modifier,
    thickness : Dp = .5.dp,
    color : Color = BotStacks.colorScheme
        .border.copy(alpha = .25f)
) {
    Divider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
@NonRestartableComposable
internal fun CupertinoVerticalDivider(
    modifier: Modifier = Modifier,
    thickness : Dp = .5.dp,
    color : Color = BotStacks.colorScheme
        .border.copy(alpha = .25f)
) {
    VerticalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

@Composable
internal fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = DividerDefaults.color,
) {
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    Box(
        modifier
            .fillMaxHeight()
            .width(targetThickness)
            .background(color = color)
    )
}