/*
 * Copyright (c) 2023.
 */

package io.inappchat.sdk.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.size(size: Float) = this.size(size.dp)
fun Modifier.size(size: Double) = this.size(size.dp)
fun Modifier.size(size: Int) = this.size(size.dp)
fun Modifier.radius(size: Float) = clip(RoundedCornerShape(size.dp))
fun Modifier.radius(size: Int) = clip(RoundedCornerShape(size.dp))
fun Modifier.radius(size: Dp) = clip(RoundedCornerShape(size))
fun Modifier.circle(size: Dp, color: Color = Color.Transparent) =
    size(size).background(color, CircleShape)


@Composable
fun Center(modifier: Modifier = Modifier.clipToBounds(), content: @Composable BoxScope.() -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = modifier, content = content)
}

@Composable
fun Space(size: Float = 4f) {
    Spacer(modifier = Modifier.size(size.dp))
}

@Composable
fun Fill() {
    Spacer(modifier = Modifier.fillMaxSize())
}