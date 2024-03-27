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