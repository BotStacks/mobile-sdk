package ai.botstacks.sdk.ui.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

object ActionSheetDefaults {
    @OptIn(ExperimentalMaterialApi::class)
    val SheetState: ModalBottomSheetState
        @Composable get() = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
}
@OptIn(ExperimentalMaterialApi::class)
abstract class ActionSheetState(internal val sheetState: ModalBottomSheetState?) {

    var onStateChange: (Boolean) -> Unit = { }

    suspend fun show() {
        sheetState?.show()
        onStateChange(true)
    }

    suspend fun hide() {
        sheetState?.hide()
        onStateChange(false)
    }
}