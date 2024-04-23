package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.state.Message
import androidx.compose.runtime.Composable

@Composable
fun MediaActionSheet(
    state: MediaActionSheetState,
) {
    MediaActionSheetContainer(state) { onSelection ->
        MediaActionSheetContent(onSelection)
    }
}

@Composable
fun MessageActionSheet(
    state: MessageActionSheetState,
    openThread: (Message) -> Unit,
) {
    MessageActionSheetContainer(state, openThread) { onSelection ->
        MessageActionSheetContent(onSelection)
    }
}