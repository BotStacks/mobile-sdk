package ai.botstacks.sdk.ui.components

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
) {
    MessageActionSheetContainer(state) { onSelection ->
        MessageActionSheetContent(state.messageForAction, onSelection)
    }
}