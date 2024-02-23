/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.ui.adjustedHsl
import ai.botstacks.sdk.utils.ui.painterImageAsset
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ChatPlaceholder(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        BotStacks.colorScheme.primary.adjustedHsl(-25),
                        BotStacks.colorScheme.primary.adjustedHsl(25)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
                shape = RectangleShape
            )
            .size(50)
    ) {
        Icon(
            painter =  BotStacks.assets.chat?.let { painterImageAsset(it) } ?: painterResource(Res.drawable.users_three_fill),
            contentDescription = "Chat icon",
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@IPreviews
@Composable
fun ChatPlaceholderPreview() {
    ChatPlaceholder()
}