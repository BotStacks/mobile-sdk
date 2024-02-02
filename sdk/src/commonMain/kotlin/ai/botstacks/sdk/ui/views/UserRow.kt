package ai.botstacks.sdk.ui.views

import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    onClick: () -> Unit,
) {
    UserRow(
        modifier = modifier,
        avatar = { Avatar(type = AvatarType.User(user.avatar, user.status)) },
        displayName = user.displayNameFb,
        onClick = onClick
    )
}

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    url: String?,
    displayName: String,
    onClick: () -> Unit,
) {
    UserRow(
        modifier = modifier,
        avatar = { Avatar(type = AvatarType.User(url)) },
        displayName = displayName,
        onClick = onClick
    )
}

@Composable
fun UserRow(
    modifier: Modifier = Modifier,
    displayName: String,
    avatar: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
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