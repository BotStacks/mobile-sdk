package ai.botstacks.sdk.ui.views

import ai.botstacks.sdk.ui.utils.measuredThemedViewController
import platform.UIKit.UIViewController


fun _ChannelSettingsView(
    state: ChannelSettingsState,
    onAddUsers: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    ChannelSettingsView(
        state = state,
        onAddUsers = onAddUsers,
    )
}

fun _CreateChannelView(
    state: CreateChannelState,
    onSelectUsers: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    CreateChannelView(
        state = state,
        onSelectUsers = onSelectUsers,
    )
}

fun _SelectChannelUsersView(
    state: ChannelUserSelectionState,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    SelectChannelUsersView(state)
}