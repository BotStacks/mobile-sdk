package ai.botstacks.sample.kmp.navigation

import androidx.compose.runtime.Composable


@Composable
internal expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)