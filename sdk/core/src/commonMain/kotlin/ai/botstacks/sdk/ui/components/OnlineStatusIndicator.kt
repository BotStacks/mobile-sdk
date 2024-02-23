package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.LocalBotStacksColorPalette
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnlineStatusIndicator(
    modifier: Modifier = Modifier,
    status: OnlineStatus
) {
    val palette = LocalBotStacksColorPalette.current

    Box(
        modifier = Modifier.border(width = 2.dp, color = BotStacks.colorScheme.background, CircleShape)
            .padding(2.dp)
            .size(12.dp)
            .background(
                when (status) {
                    OnlineStatus.Away -> palette.dark._100
                    OnlineStatus.DND -> BotStacks.colorScheme.background
                    OnlineStatus.Offline -> palette.red._500
                    OnlineStatus.Online -> palette.green._800
                    OnlineStatus.UNKNOWN__ -> BotStacks.colorScheme.background
                }, CircleShape
            ).then(modifier)
    )
}
