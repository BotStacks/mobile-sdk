package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
internal actual fun ToggleSwitch(
    modifier: Modifier,
    thumbContent: (@Composable () -> Unit)?,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        thumbContent = thumbContent,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}