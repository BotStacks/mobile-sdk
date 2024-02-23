package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.LocalBotStacksShapes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin

interface OverflowMenuScope {

    fun item(
        content: @Composable (PaddingValues) -> Unit
    )

    fun label(
        enabled: Boolean = true,
        onClick: () -> Unit,
        icon: (@Composable () -> Unit)? = null,
        subtitle: @Composable () -> Unit = { },
        title: @Composable () -> Unit,
    )
}

@ExperimentalAnimationApi
@Composable
fun OverflowMenu(
    key: Any? = null,
    visible : Boolean,
    onDismissRequest : () -> Unit,
    enterTransition : EnterTransition = scaleIn(
        animationSpec = tween(
            durationMillis = 100
        ),
        transformOrigin = TransformOrigin(.5f, 0f)
    ),
    exitTransition: ExitTransition = scaleOut(
        animationSpec = tween(
            durationMillis = 100
        ),
        transformOrigin = TransformOrigin(.5f, 0f)
    ),
    alignment: Alignment.Horizontal = Alignment.End,
    menu : OverflowMenuScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = DropdownOverflowMenu(
    key = key,
    visible = visible,
    onDismissRequest = onDismissRequest,
    menu = menu,
    modifier = modifier,
    content = content
)

@Composable
private fun DropdownOverflowMenu(
    key: Any?,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    menu: OverflowMenuScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = remember(menu, key) {
        OverflowMenuScopeImpl(onDismissRequest).apply(menu)
    }

    Box(modifier) {
        content()
        // override M3 extraSmall and surface color to match our menu spec
        MaterialTheme(
            shapes = LocalBotStacksShapes.current.copy(extraSmall = LocalBotStacksShapes.current.medium)
        ) {
            DropdownMenu(
                modifier = Modifier
                    .background(BotStacks.colorScheme.background),
                expanded = visible,
                onDismissRequest = onDismissRequest,
            ) {
                scope.items.forEach {
                    it(MenuDefaults.DropdownMenuItemContentPadding)
                }
            }
        }
    }
}

private class OverflowMenuScopeImpl(private val onDismissRequest: () -> Unit) : OverflowMenuScope {

    val items = mutableListOf<@Composable (PaddingValues) -> Unit>()

    override fun item(content: @Composable (PaddingValues) -> Unit) {
        items.add(content)
    }

    override fun label(
        enabled: Boolean,
        onClick: () -> Unit,
        icon: (@Composable () -> Unit)?,
        subtitle: @Composable () -> Unit,
        title: @Composable () -> Unit,
    ) = item {
        DropdownMenuItem(
            text = title,
            leadingIcon = icon,
            enabled = enabled,
            onClick = {
                onClick()
                onDismissRequest()
            },
            colors = MenuDefaults.itemColors(
                textColor = BotStacks.colorScheme.onBackground,
            )
        )
    }
}