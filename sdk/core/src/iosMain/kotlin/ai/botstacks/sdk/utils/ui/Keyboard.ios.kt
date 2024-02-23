package ai.botstacks.sdk.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification

@Composable
actual fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val keyboardObserver = remember { KeyboardObserver { keyboardState.value = it } }
    DisposableEffect(LocalUIViewController.current) {
        keyboardObserver.watch()
        onDispose { keyboardObserver.stop() }
    }

    return keyboardState
}

private class KeyboardObserver(
    private val onStateChanged: (Boolean) -> Unit,
) {
    fun watch() {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = null,
        ) {
            onStateChanged(false)
        }
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIKeyboardWillShowNotification,
            `object` = null,
            queue = null,
        ) {
            onStateChanged(false)
        }
    }

    fun stop() {
        NSNotificationCenter.defaultCenter.removeObserver(this)
    }
}