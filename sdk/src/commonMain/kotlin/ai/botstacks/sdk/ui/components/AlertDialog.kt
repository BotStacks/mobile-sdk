package ai.botstacks.sdk.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
expect fun applyAlertActionStyle(actionStyle: AlertActionStyle, textStyle: TextStyle, dark: Boolean): TextStyle

sealed interface AlertActionStyle {
    @Composable
    fun apply(style: TextStyle, dark: Boolean): TextStyle
    data object Default: AlertActionStyle {
        @Composable
        override fun apply(style: TextStyle, dark: Boolean): TextStyle {
            return applyAlertActionStyle(this, style, dark)
        }
    }
    data object Cancel: AlertActionStyle {
        @Composable
        override fun apply(style: TextStyle, dark: Boolean): TextStyle {
            return applyAlertActionStyle(this, style, dark)
        }
    }
    data object Destructive: AlertActionStyle {
        @Composable
        override fun apply(style: TextStyle, dark: Boolean): TextStyle {
            return applyAlertActionStyle(this, style, dark)
        }
    }
}

interface NativeAlertDialogButtonsScope {

    fun button(
        onClick : () -> Unit,
        style : AlertActionStyle = AlertActionStyle.Default,
        enabled : Boolean = true,
        title : String
    )
}

interface AlertDialogActionsScope {

    fun action(
        onClick : () -> Unit,
        style : AlertActionStyle = AlertActionStyle.Default,
        enabled : Boolean = true,
        title : @Composable () -> Unit
    )
}

@Composable
expect fun BotStacksAlertDialog(
    onDismissRequest : () -> Unit,
    title: String? = null,
    message: String? = null,
    containerColor : Color = Color.Unspecified,
    buttons : NativeAlertDialogButtonsScope.() -> Unit = {}
)