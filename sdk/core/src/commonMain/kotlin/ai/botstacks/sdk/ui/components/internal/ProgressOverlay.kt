package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.utils.ui.disableInput
import ai.botstacks.sdk.ui.BotStacks
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

/**
 * Indeterminate progress spinner on a full sized translucent background
 *
 * @param visible - boolean controlling whether the widget is visible
 * @param enter - transition for when the progress overlay enters composition
 * @param exit - transition for when the progress overlay exits composition
 * @param touchBlocking - whether tap events are allowed to filter through or not. Default
 *     is false for backward compatibility. Consider if the default should be true.
 */
@Composable
internal fun ProgressOverlay(
    visible: Boolean,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    touchBlocking: Boolean = false,
    overlayColor: Color = BotStacks.colorScheme.onBackground.copy(alpha = 0.5f),
) {
    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
    ) {
        Box(
            modifier = Modifier
                .testTag("RediProgressOverlay")
                .fillMaxSize()
                .background(color = overlayColor)
                .disableInput(touchBlocking),
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
