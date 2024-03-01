package ai.botstacks.sdk.internal.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize

@Composable
internal expect fun calculateScreenSize(): DpSize

internal fun IntSize.toDp(density: Density): DpSize = with(density) { DpSize(width = width.toDp(), height = height.toDp()) }