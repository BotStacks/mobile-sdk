/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews

@Composable
fun Spinner() {
    CircularProgressIndicator(color = colorScheme.primary)
}

@Composable
fun SpinnerList() {
    Box(
        modifier = Modifier
          .padding(64.dp)
          .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = colorScheme.primary)
    }
}

@IPreviews
@Composable
fun SpinnerPreview() {
    BotStacksChatContext {
        Spinner()
    }
}