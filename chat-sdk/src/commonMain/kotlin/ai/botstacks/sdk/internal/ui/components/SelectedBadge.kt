package ai.botstacks.sdk.internal.ui.components

import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.botstacks.`chat-sdk`.generated.resources.Res
import dev.icerock.moko.resources.compose.painterResource

@Composable
internal fun SelectedBadge(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .size(BotStacks.dimens.staticGrid.x4)
            .background(BotStacks.colorScheme.primary, CircleShape).then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.padding(BotStacks.dimens.staticGrid.x1),
            painter = painterResource(Res.images.check),
            contentDescription = "is selected",
            tint = Color.White
        )
    }
}