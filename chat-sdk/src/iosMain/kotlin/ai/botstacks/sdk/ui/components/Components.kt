package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.utils.ui.composeColor
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.ui.theme.painterImageAsset
import ai.botstacks.sdk.ui.utils.IntrinsicWidthUIKitView
import ai.botstacks.sdk.ui.utils.measuredThemedViewController
import androidx.compose.material.Icon
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIView
import platform.UIKit.UIViewController


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

fun _ChannelRow(
    chat: Chat,
    showMemberPreview: Boolean = false,
    titleFontStyle: FontStyle? = null,
    titleColor: UIColor? = null,
    subtitleFontStyle: FontStyle? = null,
    subtitleColor: UIColor? = null,
    onClick: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {

    val titleTextStyle = titleFontStyle ?: BotStacks.fonts.body1
    val titleTextColor = titleColor?.composeColor ?: BotStacks.colorScheme.onBackground
    val subtitleTextStyle = subtitleFontStyle ?: BotStacks.fonts.body1
    val subtitleTextColor = subtitleColor?.composeColor ?: BotStacks.colorScheme.caption

    ChannelRow(
        chat = chat,
        showMemberPreview = showMemberPreview,
        titleFontStyle = titleTextStyle,
        titleColor = titleTextColor,
        subtitleFontStyle = subtitleTextStyle,
        subtitleColor = subtitleTextColor,
        onClick = onClick
    )
}

fun _ChannelRow(
    imageUrls: List<String>,
    title: String,
    titleFontStyle: FontStyle? = null,
    titleColor: UIColor? = null,
    subtitle: String? = null,
    subtitleFontStyle: FontStyle? = null,
    subtitleColor: UIColor? = null,
    onClick: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {

    val titleTextStyle = titleFontStyle ?: BotStacks.fonts.body1
    val titleTextColor = titleColor?.composeColor ?: BotStacks.colorScheme.onBackground
    val subtitleTextStyle = subtitleFontStyle ?: BotStacks.fonts.body1
    val subtitleTextColor = subtitleColor?.composeColor ?: BotStacks.colorScheme.caption

    ChannelRow(
        imageUrls = imageUrls,
        title = title,
        titleColor = titleTextColor,
        titleFontStyle = titleTextStyle,
        subtitle = subtitle,
        subtitleColor = subtitleTextColor,
        subtitleFontStyle = subtitleTextStyle,
        onClick = onClick
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
    endAction: HeaderEndAction? = null,
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
            endAction?.let { action ->
                when (action) {
                    is HeaderEndAction.Next -> HeaderDefaults.NextAction(action.onClick)
                    is HeaderEndAction.Create -> HeaderDefaults.CreateAction(action.onClick)
                    is HeaderEndAction.Menu -> HeaderDefaults.MenuAction(action.onClick)
                    is HeaderEndAction.Save -> HeaderDefaults.SaveAction(action.onClick)
                }
            }
        }
    )
}