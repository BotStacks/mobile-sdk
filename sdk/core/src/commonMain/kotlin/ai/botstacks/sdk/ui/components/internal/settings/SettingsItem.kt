@file:OptIn(ExperimentalResourceApi::class)

package ai.botstacks.sdk.ui.components.internal.settings

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.utils.ui.addIfNonNull
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

internal interface SettingsSectionScope {
    fun item(
        icon: DrawableResource? = null,
        title: String,
        subtitle: String? = null,
        endSlot: @Composable () -> Unit = { },
        onClick: (() -> Unit)? = null,
    )

    fun item(
        icon: (@Composable () -> Unit)? = null,
        title: @Composable () -> Unit,
        subtitle: (@Composable () -> Unit)? = null,
        endSlot: @Composable () -> Unit = { },
        onClick: (() -> Unit)? = null,
    )

    fun divider()

    @Composable
    fun Icon(painter: Painter) {
        Icon(
            modifier = Modifier
                .size(BotStacks.dimens.staticGrid.x6),
            painter = painter,
            contentDescription = null
        )
    }
}

private class SettingsSectionScopeImpl : SettingsSectionScope {

    val items = mutableListOf<@Composable () -> Unit>()

    override fun item(
        icon: DrawableResource?,
        title: String,
        subtitle: String?,
        endSlot: @Composable () -> Unit,
        onClick: (() -> Unit)?,
    ) {
        items += {
            SettingsItem(
                title = title,
                subtitle = subtitle,
                icon = icon?.let { { Icon(painterResource(it)) } },
                endSlot = endSlot,
                onClick = onClick,
            )
        }
    }


    override fun item(
        icon: (@Composable () -> Unit)?,
        title: @Composable () -> Unit,
        subtitle: (@Composable () -> Unit)?,
        endSlot: @Composable () -> Unit,
        onClick: (() -> Unit)?
    ) {
        items += {
            SettingsItem(
                title = title,
                subtitle = subtitle,
                icon = icon,
                endSlot = endSlot,
                onClick = onClick,
            )
        }
    }

    override fun divider() {
        items += { Divider(color = BotStacks.colorScheme.border) }
    }
}

@Composable
internal fun SettingsItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    icon: (@Composable () -> Unit)? = null,
    subtitle: (@Composable () -> Unit)? = null,
    endSlot: @Composable () -> Unit = { },
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .addIfNonNull(onClick) { Modifier.clickable { it() } }
            .padding(vertical = BotStacks.dimens.staticGrid.x4,)
            .padding(horizontal = BotStacks.dimens.inset),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.invoke()
        Column(
            modifier = Modifier
                .padding(start = BotStacks.dimens.staticGrid.x2)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            title()
            subtitle?.invoke()
        }
        Box(
            modifier = Modifier
                .padding(
                    end = if (onClick == null) BotStacks.dimens.staticGrid.x2 else 0.dp
                )
        ) {
            endSlot()
        }
    }
}

@Composable
internal fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    endSlot: @Composable () -> Unit = { },
    onClick: (() -> Unit)? = null,
) {
    SettingsItem(
        modifier = modifier,
        icon = icon,
        title = { Text(title, fontStyle = BotStacks.fonts.label2) },
        subtitle = subtitle?.let {
            {
                Text(
                    it,
                    color = BotStacks.colorScheme.onBackground.copy(alpha = 0.54f),
                    fontStyle = BotStacks.fonts.caption1
                )
            }
        },
        endSlot = endSlot,
        onClick = onClick,
    )
}

@Composable
internal fun SettingsSection(
    modifier: Modifier = Modifier,
    content: SettingsSectionScope.() -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        val scope = SettingsSectionScopeImpl()
            .apply(content)
            .items

        scope.forEach { item ->
            item()
        }
    }
}