package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.ui.theme.LocalBotStacksColorPalette
import ai.botstacks.sdk.utils.IPreviews
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Shape


@Composable
fun Badge(
    count: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalBotStacksColorPalette.current
    if (count > 0) {
        val text = when {
            count in 1..99 -> "$count"
            else -> "99+"
        }

        val contentPadding = when {
            count in 1..99 -> PaddingValues(
                horizontal = BotStacks.dimens.grid.x2,
                vertical = BotStacks.dimens.grid.x1
            )

            else -> PaddingValues(
                horizontal = BotStacks.dimens.grid.x3,
                vertical = BotStacks.dimens.grid.x1
            )
        }
        Badge(
            modifier = modifier,
            label = text,
            contentPadding = contentPadding,
            backgroundColor = palette.primary._800,
            contentColor = Color.White,
            fontStyle = BotStacks.fonts.caption2.copy(size = 10.sp)
        )
    }
}

@Composable
fun Badge(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BotStacks.colorScheme.primary,
    contentColor: Color = BotStacks.colorScheme.onPrimary,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = BotStacks.dimens.grid.x2,
        vertical = BotStacks.dimens.grid.x1
    ),
    fontStyle: FontStyle = BotStacks.fonts.caption2,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = shape)
            .padding(contentPadding),
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = contentColor,
            style = fontStyle.asTextStyle(),
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@IPreviews
@Composable
fun BadgePreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Badge(count = 1)
        Badge(count = 101)
    }
}
