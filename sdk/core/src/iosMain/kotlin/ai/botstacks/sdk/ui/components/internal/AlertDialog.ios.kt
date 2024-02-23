package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.theme.AppleColors
import ai.botstacks.sdk.utils.ui.applyTheme
import ai.botstacks.sdk.utils.ui.ui
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyle
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertActionStyleDestructive
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIView
import platform.UIKit.UIViewController

@Composable
internal actual fun applyAlertActionStyle(actionStyle: AlertActionStyle, textStyle: TextStyle, dark: Boolean): TextStyle {
    return when (actionStyle) {
        is AlertActionStyle.Default -> {
            return textStyle.copy(
                fontWeight = FontWeight.Normal,
                color = AppleColors.blue(dark),
                textAlign = TextAlign.Center
            )
        }
        is AlertActionStyle.Cancel -> {
            return textStyle.copy(
                fontWeight = FontWeight.Bold,
                color = AppleColors.blue(dark),
                textAlign = TextAlign.Center
            )
        }
        is AlertActionStyle.Destructive -> {
            return textStyle.copy(
                fontWeight = FontWeight.Normal,
                color = AppleColors.red(dark),
                textAlign = TextAlign.Center
            )
        }
        else -> textStyle
    }
}

@Composable
@NonRestartableComposable
internal actual fun BotStacksAlertDialog(
    onDismissRequest : () -> Unit,
    title: String?,
    message: String?,
    containerColor : Color,
    buttons : NativeAlertDialogButtonsScope.() -> Unit
) {
    UIAlertController(
        onDismissRequest = onDismissRequest,
        title = title,
        message = message,
        style = UIAlertControllerStyleAlert,
        buttons = buttons,
        containerColor = containerColor
    )
}

@Composable
internal fun UIAlertController(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    style: UIAlertActionStyle,
    containerColor : Color,
    buttons: NativeAlertDialogButtonsScope.() -> Unit,
) {

    PresentableDialog(
        factory = {
            UIAlertController.alertControllerWithTitle(
                title = title,
                message = message,
                preferredStyle = style
            ).apply {
                NativeAlertDialogButtonsScopeCupertino(onDismissRequest)
                    .apply(buttons)
                    .buttons.forEach {
                        addAction(it)
                    }
            }
        },
        update = {
            setTitle(title)
            setMessage(message)
            if (containerColor.isSpecified) {
                // the design is very human
                val first = view.subviews.firstOrNull() as? UIView
                val second = first?.subviews?.firstOrNull() as? UIView
                second?.backgroundColor = containerColor.ui
                first?.clipsToBounds = true
            }
        },
        onDismissRequest = onDismissRequest,
        title, message
    )
}

@Composable
internal fun <T : UIViewController> PresentableDialog(
    factory : () -> T,
    update : T.() -> Unit,
    onDismissRequest: () -> Unit,
    vararg updateKeys : Any?
){
    val dark = isSystemInDarkTheme()

    val presentController = remember {
        factory()
    }

    val controller = LocalUIViewController.current

    val presentMutex = remember(presentController) { Mutex(locked = true) }

    LaunchedEffect(dark, *updateKeys){
        presentController.applyTheme(dark)
        update(presentController)
    }

    LaunchedEffect(presentController) {
        presentMutex.withLock {
            //TODO: replace with transitioningDelegate or smth more optimized
            while (true) {
                delay(100)
                if (controller.presentedViewController != presentController) {
                    onDismissRequest()
                    return@withLock
                }
            }
        }
    }

    DisposableEffect(0) {
        controller.presentViewController(presentController, true){
            if (presentMutex.isLocked) {
                presentMutex.unlock()
            }
        }
        onDispose {
            if (controller.presentedViewController == presentController) {
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }
}

private class NativeAlertDialogButtonsScopeCupertino(
    val onDismissRequest: () -> Unit,
) : NativeAlertDialogButtonsScope {

    val buttons = mutableListOf<UIAlertAction>()

    override fun button(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: String
    ) {
        buttons.add(UIAlertAction.actionWithTitle(
            title = title, style = style.ui, handler = {
                onClick()
                onDismissRequest()
            }
        ).apply {
            setEnabled(enabled)
        })
    }
}

private val AlertActionStyle.ui : UIAlertActionStyle get() =
    when(this){
        AlertActionStyle.Default -> UIAlertActionStyleDefault
        AlertActionStyle.Cancel -> UIAlertActionStyleCancel
        AlertActionStyle.Destructive -> UIAlertActionStyleDestructive
    }