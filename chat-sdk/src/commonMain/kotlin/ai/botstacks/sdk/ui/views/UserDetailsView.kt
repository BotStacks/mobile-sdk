package ai.botstacks.sdk.ui.views

import ai.botstacks.sdk.internal.actions.toggleBlock
import ai.botstacks.sdk.internal.actions.toggleMute
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.ui.components.ToggleSwitch
import ai.botstacks.sdk.internal.ui.components.settings.SettingsSection
import ai.botstacks.sdk.ui.components.ChannelGroup
import ai.botstacks.sdk.ui.components.UserProfile
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import ai.botstacks.`chat-sdk`.generated.resources.Res
import dev.icerock.moko.resources.compose.painterResource

/**
 * UserDetailsState
 *
 * State holder for displaying details about a User, used specifically within the [UserDetailsView].
 *
 */
class UserDetailsState(internal val user: User) {

    constructor(id: String): this(User.get(id)!!)

    internal val channelsInCommon
        get() = user.channelsInCommon

    internal val isMuted: Boolean
        get() = user.muted

    internal val isBlocked: Boolean
        get() = user.blocked

    internal fun toggleMute() {
        user.toggleMute()
    }

    internal fun toggleBlock() {
        user.toggleBlock()
    }
}

/**
 * UserDetailsView
 *
 * A screen content view for displaying details about a given [User].
 *
 * @param state the state for this details view.
 */
@Composable
fun UserDetailsView(state: UserDetailsState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(BotStacks.dimens.inset),
    ) {
        item {
            UserProfile(user = state.user)
        }

        if (state.channelsInCommon.isNotEmpty()) {
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
                        text = "${state.channelsInCommon.count()} channels in common",
                        fontStyle = BotStacks.fonts.label2
                    )
                    ChannelGroup(channels = state.channelsInCommon)
                }
            }
        }

        item {
            SettingsSection(
                modifier = Modifier.fillMaxWidth()
            ) {
                item(
                    icon = Res.images.bell_simple_fill,
                    title = "Notifications",
                    endSlot = {
                        ToggleSwitch(checked = state.isMuted, onCheckedChange = null)
                    },
                    onClick = { state.toggleMute() }
                )
                divider()
                item(
                    icon = {
                        Image(
                            painter = painterResource(Res.images.block_fill),
                            colorFilter = ColorFilter.tint(BotStacks.colorScheme.error),
                            contentDescription = null,
                        )
                    },
                    title = {
                        Text(
                            if (state.isBlocked) "Unblock" else "Block",
                            fontStyle = BotStacks.fonts.label2,
                            color = BotStacks.colorScheme.error
                        )
                    },
                    onClick = { state.toggleBlock() }
                )
            }
        }
    }
}