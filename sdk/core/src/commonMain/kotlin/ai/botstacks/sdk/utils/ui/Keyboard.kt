package ai.botstacks.sdk.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
expect fun keyboardAsState(): State<Boolean>