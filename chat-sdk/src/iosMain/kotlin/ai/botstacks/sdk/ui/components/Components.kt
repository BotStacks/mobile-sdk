package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.ui.components.EmptyListView
import ai.botstacks.sdk.internal.utils.ui.composeColor
import ai.botstacks.sdk.internal.utils.ui.debugBounds
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.MessageAttachment
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.ui.theme.painterImageAsset
import ai.botstacks.sdk.ui.utils.IntrinsicUIKitView
import ai.botstacks.sdk.ui.utils.measuredThemedViewController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
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
    chat: Chat?,
    showMemberPreview: Boolean = false,
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

    if (chat != null) {
        ChannelRow(
            chat = chat,
            showMemberPreview = showMemberPreview,
            titleColor = titleTextColor,
            titleFontStyle = titleTextStyle,
            subtitleColor = subtitleTextColor,
            subtitleFontStyle = subtitleTextStyle,
            onClick = onClick
        )
    } else {
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
}

fun _ChannelGroup(
    channels: List<Chat>,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    ChannelGroup(channels = channels)
}

fun _ChatInput(
    chat: Chat,
    onMedia: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    ChatInput(
        chat = chat,
        onMedia = onMedia,
    )
}

fun _ChatList(
    header: (() -> UIView)? = null,
    emptyState: (() -> UIView)? = null,
    filter: (Chat) -> Boolean = { true },
    onChatClicked: (Chat) -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    ChatList(
        modifier = Modifier.fillMaxSize(),
        header = {
            if (header != null) {
                IntrinsicUIKitView(
                    heightIn = true,
                    uiView = header,
                    modifier = Modifier
                )
            }
        },
        emptyState = {
            if (emptyState != null) {
                IntrinsicUIKitView(
                    uiView = emptyState,
                    modifier = Modifier
                )
            } else {
                EmptyListView(config = BotStacks.assets.emptyChats)
            }
        },
        filter = filter,
        onChatClicked = onChatClicked
    )
}

fun _ChatMessage(
    message: Message,
    shapeDefinition: ShapeDefinition,
    showAvatar: Boolean = false,
    showTimestamp: Boolean = true,
    onPressUser: (User) -> Unit,
    onLongPress: () -> Unit,
    onClick: ((MessageAttachment?) -> Unit)? = null,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    val shape = when (shapeDefinition) {
        ShapeDefinition.small -> BotStacks.shapes.small
        ShapeDefinition.medium -> BotStacks.shapes.medium
        ShapeDefinition.large -> BotStacks.shapes.large
    }

    ChatMessage(
        message = message,
        shape = shape,
        showAvatar = showAvatar,
        showTimestamp = showTimestamp,
        onPressUser = onPressUser,
        onLongPress = onLongPress,
        onClick = onClick
    )
}

fun _ChatMessagePreview(
    chat: Chat,
    onClick: () -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    ChatMessagePreview(chat = chat, onClick = onClick)
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
    menu: (() -> UIView)? = null,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured = onMeasured) {
    Header(
        state = state,
        title = {
            if (titleSlot != null) {
                IntrinsicUIKitView(
                    backgroundColor = BotStacks.colorScheme.header,
                    uiView = titleSlot,
                    modifier = Modifier.height(HeaderHeight)
                )
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
            if (menu != null) {
                IntrinsicUIKitView(
                    backgroundColor = BotStacks.colorScheme.header,
                    uiView = menu,
                    modifier = Modifier.height(HeaderHeight)
                )
                return@Header
            }

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

fun _MessageList(
    chat: Chat,
    header: (() -> UIView)? = null,
    emptyState: (() -> UIView)? = null,
    onPressUser: (User) -> Unit,
    onLongPress: (Message) -> Unit,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    MessageList(
        modifier = Modifier.fillMaxSize(),
        chat = chat,
        header = {
            if (header != null) {
                IntrinsicUIKitView(
                    modifier = Modifier.height(HeaderHeight).debugBounds(),
                    uiView = header
                )
            }
        },
        emptyState = {
            if (emptyState != null) {
                IntrinsicUIKitView(modifier = Modifier.fillMaxSize(), uiView = emptyState)
            } else {
                EmptyListView(config = BotStacks.assets.emptyChat)
            }
        },
        onLongPress = onLongPress,
        onPressUser = onPressUser
    )
}

fun _Spinner(
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    Spinner()
}

fun _UserProfile(
    user: User,
    onMeasured: (Double, Double) -> Unit,
): UIViewController = measuredThemedViewController(onMeasured) {
    UserProfile(user = user)
}