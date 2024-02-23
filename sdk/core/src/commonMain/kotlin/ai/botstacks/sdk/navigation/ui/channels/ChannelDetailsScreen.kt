package ai.botstacks.sdk.navigation.ui.channels

import ai.botstacks.sdk.ui.components.internal.ProgressOverlay
import ai.botstacks.sdk.ui.components.ChannelSettingsState
import ai.botstacks.sdk.ui.components.ChannelSettingsView
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.utils.ui.keyboardAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ChannelDetailsScreen(
    state: ChannelSettingsState,
    onBackClicked: () -> Unit,
    onOpenAnnouncements: () -> Unit,
    onAddUsers: () -> Unit,
) {
    val composeScope = rememberCoroutineScope()

    val keyboardVisible by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Header(
                title = "Channel Details",
                onBackClicked = onBackClicked,
                endAction = {
                    HeaderDefaults.SaveAction {
                        composeScope.launch {
                            if (keyboardVisible) {
                                keyboardController?.hide()
                                delay(300.milliseconds)
                            }

                            val result = state.update()
                            if (result.isSuccess) {
                                onBackClicked()
                            }
                        }
                    }
                }
            )

            ChannelSettingsView(state, onOpenAnnouncements, onAddUsers)
        }
        ProgressOverlay(visible = state.saving, touchBlocking = true)
    }
}