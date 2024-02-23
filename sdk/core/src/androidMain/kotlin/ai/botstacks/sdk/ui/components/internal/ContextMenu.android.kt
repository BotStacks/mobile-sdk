package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.ContextMenuScope
import ai.botstacks.sdk.ui.theme.LocalBotStacksShapes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
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

@ExperimentalAnimationApi
@Composable
actual fun ContextMenu(
    key: Any?,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    alignment: Alignment.Horizontal,
    menu: ContextMenuScope.() -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) = DropdownContextMenu(
    key = key,
    visible = visible,
    onDismissRequest = onDismissRequest,
    menu = menu,
    modifier = modifier,
    content = content
)

private class ContextMenuScopeImpl(private val onDismissRequest: () -> Unit) : ContextMenuScope {

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

@Composable
fun DropdownContextMenu(
    key: Any?,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    menu: ContextMenuScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = remember(menu, key) {
        ContextMenuScopeImpl(onDismissRequest).apply(menu)
    }

    Box(modifier) {
        content()
        // override M3 extraSmall to match our menu spec
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