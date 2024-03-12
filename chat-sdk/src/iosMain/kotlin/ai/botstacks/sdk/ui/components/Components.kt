package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.ui.theme.BotStacksTheme
import ai.botstacks.sdk.ui.theme.painterImageAsset
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.window.ComposeUIViewController
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
        title = { title?.let { HeaderDefaults.Title(title) } },
        icon = {
            val showIcon = title == null && onBackClicked == null
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
                UIKitView(factory = { view() }, modifier = Modifier)
            }
        }
    )
}