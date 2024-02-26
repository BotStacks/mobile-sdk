/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable()
internal fun ColumnScope.GrowSpacer() = Spacer(modifier = Modifier.weight(1f))

@Composable
internal fun RowScope.GrowSpacer() = Spacer(modifier = Modifier.weight(1f))
