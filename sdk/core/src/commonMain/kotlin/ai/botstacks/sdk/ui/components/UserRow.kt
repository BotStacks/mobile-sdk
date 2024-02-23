package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    user: User,
    endSlot: @Composable () -> Unit = { },
    onClick: (() -> Unit)? = null
) {
    UserRow(
        modifier = modifier,
        avatar = { Avatar(type = AvatarType.User(user.avatar, user.status)) },
        displayName = user.displayNameFb,
        endSlot = endSlot,
        onClick = onClick
    )
}

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    url: String?,
    displayName: String,
    endSlot: @Composable () -> Unit = { },
    onClick: (() -> Unit)? = null
) {
    UserRow(
        modifier = modifier,
        avatar = { Avatar(type = AvatarType.User(url)) },
        displayName = displayName,
        endSlot = endSlot,
        onClick = onClick
    )
}

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    displayName: String,
    avatar: @Composable () -> Unit,
    endSlot: @Composable () -> Unit = { },
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 10.dp).then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        avatar()
        Text(
            text = displayName,
            fontStyle = BotStacks.fonts.body1,
            color = BotStacks.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        endSlot()
    }
}

@IPreviews
@Composable
fun UserRow_Preview() {
    BotStacksChatContext {
        Column(Modifier.background(BotStacks.colorScheme.background)) {
            UserRow(
                modifier = Modifier.fillMaxWidth(),
                url = "https://source.unsplash.com/featured/300x200",
                displayName = "John Doe",
                onClick = { }
            )
            UserRow(
                modifier = Modifier.fillMaxWidth(),
                url = null,
                displayName = "John Doe",
                onClick = {}
            )
        }
    }
}