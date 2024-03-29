package ai.botstacks.sdk.internal.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex

@ExperimentalAnimationApi
@Composable
internal actual inline fun ContextMenu(
    key: Any?,
    visible: Boolean,
    noinline onDismissRequest: () -> Unit,
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    alignment: Alignment.Horizontal,
    noinline menu: ContextMenuScope.() -> Unit,
    modifier: Modifier,
    noinline content: @Composable () -> Unit
) = CupertinoContextMenu(
    key = key,
    visible = visible,
    onDismissRequest = onDismissRequest,
    menu = menu,
    modifier = modifier,
    content = content
)

/**
 * Container of [CupertinoContextMenu].
 * [content] is blurred when any menus inside this container are shown.
 * All types of application already provide this container.
 * */
@Composable
internal fun ContextMenuContainer(
    content: @Composable () -> Unit
) {
    val provider = remember { ContextMenuProviderImpl() }


    val animatedBlur by animateDpAsState(
        if (provider.visible) CupertinoContextMenuTokens.MenuBlurRadius else 0.dp
    )

    CompositionLocalProvider(
        LocalContextMenuProvider provides provider
    ) {
        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    provider.layoutCoordinates = it
                }
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .blur(animatedBlur)
                    .then(if (provider.visible)
                        Modifier
                            .pointerInput(provider) {
                                detectTapGestures {
                                    provider.content.onDismissRequest()
                                }
                            }
                    else Modifier)
            ) {
                content()
                if (provider.visible) {
                    Box(Modifier
                        .zIndex(Float.MAX_VALUE)
                        .width(maxWidth)
                        .height(maxHeight)
                        .pointerInput(0) {
                            // prevent pointer input of content
                        }
                    )
                }
            }

            val density = LocalDensity.current

            val shouldShowMenuContent by remember {
                derivedStateOf {
                    provider.visible || animatedBlur > 0.dp
                }
            }

            Box(
                modifier = Modifier
                    .offset { provider.content.offset.round() }
            ) {
                if (shouldShowMenuContent) {
                    with(density) {
                        Box(
                            modifier = Modifier
                                .size(provider.content.size.toSize().toDpSize()),
                        ) {
                            provider.content.content()
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .offset {
                        provider.content.offset.round() +
                                IntOffset(-(provider.content.size.width / 2), provider.content.size.height)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = SectionTokens.HorizontalPadding)
            ) {
                val scope = remember(provider.content.menu) {
                    ContextMenuScopeCupertinoImpl().apply(provider.content.menu)
                }
                AnimatedVisibility(
                    modifier = Modifier
                        .align(provider.content.alignment),
                    visible = provider.visible,
                    enter = provider.content.enterTransition,
                    exit = provider.content.exitTransition
                ) {
                    scope.Content()
                }
            }
        }
    }
}

/**
 * Cupertino context menu.
 *
 * Must be called inside [ContextMenuContainer] that will be blurred.
 * All types of application already provide such container.
 *
 * @param visible is context menu currently visible
 * @param onDismissRequest called when context menu want's to be dismissed
 * @param menu builder scope of the menu
 * @param enterTransition enter transition of menu block
 * @param exitTransition exit transition of menu block
 * @param alignment horizontal alignment of the context menu relative to [content]
 * @param enableHapticFeedback add iOS-like haptic feedbacks for menu interactions
 * @param modifier modifier of the [content] container
 * @param content content that will call menu
 * */
@ExperimentalAnimationApi
@Composable
internal fun CupertinoContextMenu(
    key: Any? = null,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    menu: ContextMenuScope.() -> Unit,
    enterTransition: EnterTransition = CupertinoContextMenuTokens.enterTransition,
    exitTransition: ExitTransition = CupertinoContextMenuTokens.exitTransition,
    alignment: Alignment.Horizontal = Alignment.End,
    enableHapticFeedback: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val provider = LocalContextMenuProvider.current

    var position by remember {
        mutableStateOf(Offset.Zero)
    }

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(key, size, position, menu, visible, alignment, exitTransition, enterTransition) {
        if (visible) {
            if (!provider.visible && enableHapticFeedback) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            provider.show(
                ContextMenuContent(
                    size = size,
                    offset = position,
                    menu = menu,
                    content = content,
                    alignment = alignment,
                    enterTransition = enterTransition,
                    exitTransition = exitTransition,
                    onDismissRequest = onDismissRequest
                )
            )
        } else {
            provider.dismiss()
        }
    }

    DisposableEffect(0) {
        onDispose {
            provider.dismiss()
        }
    }

    Box(modifier = modifier
        .onGloballyPositioned {
            val containerInWindow = provider.layoutCoordinates
                ?.positionInWindow() ?: Offset.Zero

            position = it.positionInWindow() - containerInWindow
            size = it.size
        }.onSizeChanged {
            size = it
        }
    ) {
        content()
    }
}


private val LocalContextMenuProvider = staticCompositionLocalOf<ContextMenuProvider> {
    error("Context menu container is not set")
}

private class ContextMenuContent(
    val size: IntSize,
    val offset: Offset,
    val onDismissRequest: () -> Unit,
    val menu: ContextMenuScope.() -> Unit,
    val content: @Composable () -> Unit,
    val alignment: Alignment.Horizontal,
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition
)

private interface ContextMenuProvider {

    val visible: Boolean

    val layoutCoordinates: LayoutCoordinates?

    fun show(content: ContextMenuContent)

    fun dismiss()
}

private class ContextMenuScopeCupertinoImpl : ContextMenuScope {

    private val items = mutableListOf<@Composable (PaddingValues) -> Unit>()

    override fun item(content: @Composable (PaddingValues) -> Unit) {
        items.add(content)
    }


    override fun label(
        enabled: Boolean,
        onClick: () -> Unit,
        icon: (@Composable () -> Unit)?,
        subtitle: @Composable () -> Unit,
        title: @Composable () -> Unit
    ) = row(
        title = title,
        subtitle = subtitle,
        content = icon?.let { { it() } } ?: { },
        onClick = onClick,
        enabled = enabled
    )


    @Composable
    fun Content() {
        CupertinoSection(
            modifier = Modifier.width(CupertinoContextMenuTokens.MenuWidth)
        ) {
            items.forEach { content ->
                item(
                    dividerPadding = 0.dp
                ) {
                    content.invoke(it)
                }
            }
        }
    }

    private fun row(
        modifier: Modifier = Modifier,
        enabled: Boolean,
        onClick: () -> Unit,
        title: @Composable () -> Unit,
        subtitle: @Composable () -> Unit = {},
        content: @Composable () -> Unit,
    ) = item {
        Row(
            modifier = modifier
                .heightIn(SectionTokens.MinHeight)
                .fillMaxWidth()
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    role = Role.Button,
                )
                .padding(it),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                    title()
                }
                ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                    subtitle()
                }
            }

            content()
        }
    }
}

private class ContextMenuProviderImpl : ContextMenuProvider {


    var content: ContextMenuContent by mutableStateOf(ContextMenuContent(
        size = IntSize.Zero,
        offset = Offset.Zero,
        menu = {},
        content = {},
        alignment = Alignment.End,
        enterTransition = EnterTransition.None,
        exitTransition = ExitTransition.None,
        onDismissRequest = {}
    ))

    override var visible: Boolean by mutableStateOf(false)

    override var layoutCoordinates: LayoutCoordinates? by mutableStateOf(null)

    override fun show(content: ContextMenuContent) {
        this.content = content
        visible = true
    }

    override fun dismiss() {
        visible = false
    }
}

private object CupertinoContextMenuTokens {
    val MenuBlurRadius = 50.dp
    val MenuWidth = 270.dp

    @ExperimentalAnimationApi
    val enterTransition = scaleIn(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        transformOrigin = TransformOrigin(.5f, 0f)
    )

    @ExperimentalAnimationApi
    val exitTransition = scaleOut(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        transformOrigin = TransformOrigin(.5f, 0f)
    )
}