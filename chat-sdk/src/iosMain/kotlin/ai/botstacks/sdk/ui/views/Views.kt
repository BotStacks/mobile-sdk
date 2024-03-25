package ai.botstacks.sdk.ui.views

import ai.botstacks.sdk.ui.utils.measuredThemedViewController
import platform.UIKit.UIViewController

fun _SelectChannelUsersView(
    state: ChannelUserSelectionState,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    SelectChannelUsersView(state)
}