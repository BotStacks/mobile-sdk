package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.internal.TextInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SelectChannelUsersView(
    selectedUsers: List<User>,
    onUserSelected: (User) -> Unit,
    onUserRemoved: (User) -> Unit,
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
        ) { user ->
            var isSelected by remember(selectedUsers) {
                mutableStateOf(selectedUsers.contains(user))
            }

            val handleChange = {
                isSelected = if (isSelected) {
                    onUserRemoved(user)
                    false
                } else {
                    onUserSelected(user)
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