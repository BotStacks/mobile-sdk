package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.actions.toggleBlock
import ai.botstacks.sdk.actions.toggleMute
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.internal.ToggleSwitch
import ai.botstacks.sdk.ui.components.internal.settings.SettingsSection
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.util.fastForEach
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun UserDetailsView(
    user: User
) {
    val channelsInCommon by remember(user) {
        derivedStateOf {
            user.channelsInCommon
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(BotStacks.dimens.inset),
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(BotStacks.dimens.inset),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Avatar(
                    modifier = Modifier.clip(CircleShape),
                    type = AvatarType.User(
                        url = user.avatar,
                    ),
                    size = AvatarSize.Large
                )
                Text(user.displayNameFb, fontStyle = BotStacks.fonts.h3)
            }
        }

        if (channelsInCommon.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .padding(top = BotStacks.dimens.inset)
                        .border(
                            width = BotStacks.dimens.border,
                            color = BotStacks.colorScheme.border,
                            shape = BotStacks.shapes.medium
                        ).padding(BotStacks.dimens.grid.x3),
                    verticalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x4)
                ) {
                    Text(
                        text = "${channelsInCommon.count()} channels in common",
                        fontStyle = BotStacks.fonts.label2
                    )
                    channelsInCommon.fastForEach { channel ->
                        SimpleChannelRow(
                            modifier = Modifier.fillMaxWidth(),
                            chat = channel,
                            showMemberPreview = true,
                            titleColor = BotStacks.colorScheme.primary,
                            titleFontStyle = BotStacks.fonts.caption1,
                        )
                    }
                }
            }
        }

        item {
            SettingsSection(
                modifier = Modifier.fillMaxWidth()
            ) {
                item(
                    icon = Res.drawable.bell_simple_fill,
                    title = "Notifications",
                    endSlot = {
                        ToggleSwitch(checked = user.muted, onCheckedChange = null)
                    },
                    onClick = { user.toggleMute() }
                )
                divider()
                item(
                    icon = {
                        Image(
                            painter = painterResource(Res.drawable.block_fill),
                            colorFilter = ColorFilter.tint(BotStacks.colorScheme.error),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            if (user.blocked) "Unblock" else "Block",
                            fontStyle = BotStacks.fonts.label2,
                            color = BotStacks.colorScheme.error
                        )
                    },
                    onClick = { user.toggleBlock() }
                )
            }
        }
    }
}