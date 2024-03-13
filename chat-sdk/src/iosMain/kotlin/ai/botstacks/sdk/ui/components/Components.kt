package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.utils.ui.addIf
import ai.botstacks.sdk.internal.utils.ui.debugBounds
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import ai.botstacks.sdk.ui.theme.painterImageAsset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.CValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIImage
import platform.UIKit.UIView
import platform.UIKit.UIViewController


private fun measuredThemedViewController(
    onMeasured: (Double, Double) -> Unit,
    content: @Composable () -> Unit,
): UIViewController = ComposeUIViewController {
    with(BotStacksTheme) {
        BotStacksThemeEngine(
            useDarkTheme = useDarkMode,
            lightColorScheme = lightColors,
            darkColorScheme = darkColors,
            shapes = shapes,
            assets = assets,
            fonts = fonts,
        ) {
            Box(
                modifier = Modifier.onSizeChanged {
                    onMeasured(it.width.toDouble(), it.height.toDouble())
                },
            ) {
                content()
            }
        }
    }
}

@Composable
private fun IntrinsicWidthUIKitView(uiView: UIView) {
    var width by remember(uiView) { mutableStateOf(0.dp) }
    var updateCount by remember(uiView) { mutableStateOf(0) }

    val density = LocalDensity.current
    UIKitView(
        background = BotStacks.colorScheme.header,
        factory = { uiView },
        onResize = { view, size ->
            size.useContents contents@{
                if (updateCount <= 1) {
                    width = with(density) { this@contents.size.width.toInt().toDp() }
                    updateCount++
                }
                println("width=${this@contents.size.width.toInt()}")
            }
            view.setFrame(size)
        },
        modifier = Modifier
            .addIf(width > 0.dp) { Modifier.width(width) }
            .height(HeaderHeight)
    )
}

fun _Avatar(
    size: AvatarSize = AvatarDefaults.Size,
    type: AvatarType,
    isSelected: Boolean = false,
    isRemovable: Boolean = false,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    Avatar(
        size = size,
        type = type,
        isSelected = isSelected,
        isRemovable = isRemovable,
    )
}

fun _Badge(
    count: Int,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    Badge(
        count = count
    )
}


fun _Badge(
    label: String,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    Badge(
        label = label
    )
}

fun _Header(
    state: HeaderState = HeaderState(),
    title: String? = null,
    titleSlot: (() -> UIView)? = null,
    icon: UIImage? = null,
    onSearchClicked: (() -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
    onCompose: (() -> Unit)? = null,
    onBackClicked: (() -> Unit)? = null,
    endAction: (() -> UIView)? = null,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    Header(
        state = state,
        title = {
            if (titleSlot != null) {
                IntrinsicWidthUIKitView(titleSlot())
            } else {
                title?.let { HeaderDefaults.Title(title) }
            }
        },
        icon = {
            val showIcon = title == null && onBackClicked == null && titleSlot == null
            if (showIcon) {
                icon?.let {
                    Icon(
                        painter = painterImageAsset(it),
                        contentDescription = null
                    )
                } ?: HeaderDefaults.Logo()
            }

        },
        onSearchClick = onSearchClicked,
        onAdd = onAdd,
        onCompose = onCompose,
        onBackClicked = onBackClicked,
        endAction = {
            endAction?.let { view ->
                IntrinsicWidthUIKitView(view())
            }
        }
    )
}