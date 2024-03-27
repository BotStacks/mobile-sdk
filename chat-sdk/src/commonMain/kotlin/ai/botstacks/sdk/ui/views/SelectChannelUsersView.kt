package ai.botstacks.sdk.ui.views

import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.ui.components.PagerList
import ai.botstacks.sdk.internal.ui.components.TextInput
import ai.botstacks.sdk.internal.ui.components.UserRow
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.components.Avatar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Stable
class ChannelUserSelectionState(
    initialSelections: List<User>,
) {
    constructor(chat: Chat) : this(chat.members.map { it.user })

    var selections = mutableStateListOf<User>()

    init {
        selections.addAll(initialSelections)
    }

    fun addUser(user: User) {
        selections = selections.apply { add(user) }
    }

    fun removeUser(user: User) {
        selections = selections.apply { remove(user) }
    }
}

/**
 * SelectChannelUsersView
 *
 * A screen content view for selecting users within a channel. This is used in coordination with
 * either a [CreateChannelView] or a [ChannelSettingsView] to set or update the users in a given channel.
 *
 * @param state the state holding the selected users for this view.
 *
 */
@Composable
fun SelectChannelUsersView(
    state: ChannelUserSelectionState,
) {
    var search by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(
                bottom = BotStacks.dimens.inset
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = BotStacks.dimens.inset,
                    start = BotStacks.dimens.inset,
                    end = BotStacks.dimens.inset,
                ),
            contentAlignment = Alignment.Center
        ) {
            TextInput(
                modifier = Modifier.fillMaxWidth(),
                value = search,
                onValueChanged = { search = it },
                placeholder = "Search name",
                maxLines = 1,
                fontStyle = BotStacks.fonts.body2,
                indicatorColor = BotStacks.colorScheme.caption,
            )
        }

        PagerList(
            modifier = Modifier.weight(1f),
            pager = BotStacksChatStore.current.users,
            filter = filter@{
                if (search.text.isEmpty()) return@filter true
                it.displayNameFb.contains(search.text, ignoreCase = true)
            }
        ) { user ->
            var isSelected by remember(state.selections) {
                mutableStateOf(state.selections.contains(user))
            }

            val handleChange = {
                isSelected = if (isSelected) {
                    state.removeUser(user)
                    false
                } else {
                    state.addUser(user)
                    true
                }
            }

            UserRow(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        start = BotStacks.dimens.inset,
                        end = BotStacks.dimens.inset,
                    ),
                avatar = {
                    Avatar(
                        user = user,
                        isSelected = isSelected,
                        showOnlineStatus = false,
                    )
                },
                displayName = user.displayNameFb,
                onClick = { handleChange() }
            )
        }
    }
}