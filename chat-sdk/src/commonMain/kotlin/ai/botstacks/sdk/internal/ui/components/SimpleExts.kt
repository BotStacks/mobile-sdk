/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal fun Modifier.size(size: Float) = this.size(size.dp)
internal fun Modifier.size(size: Double) = this.size(size.dp)
internal fun Modifier.size(size: Int) = this.size(size.dp)
internal fun Modifier.radius(size: Dp) = clip(RoundedCornerShape(size))
internal fun Modifier.circle(size: Dp, color: Color = Color.Transparent) =
    this.size(size)
        .radius(size / 2)
        .background(color)


@Composable
internal fun Center(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
  Box(contentAlignment = Alignment.Center, modifier = modifier, content = content)
}

@Composable
internal fun Space(size: Float = 4f) {
  Spacer(modifier = Modifier.size(size.dp))
}