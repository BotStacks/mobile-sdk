package ai.botstacks.sdk.navigation.ui.channels

import ai.botstacks.sdk.ui.components.internal.ProgressOverlay
import ai.botstacks.sdk.ui.components.CreateChannelState
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.ui.components.SetChannelDetailsView
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
fun CreateChannelScreen(
    state: CreateChannelState,
    onBackClicked: () -> Unit,
    onSelectUsers: () -> Unit,
    onChannelCreated: (String) -> Unit,
) {
    val composeScope = rememberCoroutineScope()

    val keyboardVisible by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Header(
                title = "Create Channel",
                onBackClicked = onBackClicked,
                endAction = {
                    HeaderDefaults.CreateAction {
                        composeScope.launch {
                            if (keyboardVisible) {
                                keyboardController?.hide()
                                delay(300.milliseconds)
                            }

                            val result = state.create()
                            if (result.isSuccess) {
                                val chatId = result.getOrNull()?.id
                                if (chatId != null) {
                                    onChannelCreated(chatId)
                                }
                            }
                        }
                    }
                }
            )

            SetChannelDetailsView(state, onSelectUsers)
        }
        ProgressOverlay(visible = state.saving, touchBlocking = true)
    }
}