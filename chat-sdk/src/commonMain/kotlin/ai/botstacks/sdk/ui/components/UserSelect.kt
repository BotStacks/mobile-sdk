package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.ui.theme.LocalBotStacksColorPalette
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.dayNightColor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ai.botstacks.`chat-sdk`.generated.resources.Res
import androidx.compose.foundation.layout.PaddingValues
import dev.icerock.moko.resources.compose.painterResource

/**
 * UserSelect
 *
 * A component that renders [User]'s in a horizontally scrolling Row. This is primarily used in [CreateChannelView]
 * for showing currently selected [User]s and allowing the ability to add more if desired.
 *
 * @param modifier The modifier to apply to this component
 * @param selectedUsers Currently selected users
 * @param canRemove If enabled, currently selected users can be removed on click (Will trigger [onRemove].
 * @param showAdd If enabled, a trailing add option will appear allowing you to handle [onAddSelected] to navigate to another
 * view to add users. @see [ai.botstacks.sdk.ui.views.SelectChannelUsersView] for a use case.
 * @param onRemove callback for when a user is removed.
 * @param onAddSelected callback when the trailing add option is clicked; requires [showAdd] to be true.
 *
 */
@Composable
fun UserSelect(
    modifier: Modifier = Modifier,
    selectedUsers: List<User>,
    canRemove: Boolean = false,
    showAdd: Boolean = true,
    onRemove: (User) -> Unit = { },
    onAddSelected: () -> Unit = { },
) {
    val palette = LocalBotStacksColorPalette.current
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.inset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(selectedUsers) {
            Avatar(
                modifier = Modifier
                    .clickable(enabled = canRemove) { onRemove(it) },
                user = it,
                isRemovable = canRemove,
            )
        }
        if (showAdd) {
            item {
                Avatar(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onAddSelected() },
                    type = AvatarType.User(
                        url = null,
                        empty = painterResource(Res.images.plus),
                    ),
                    backgroundColor = dayNightColor(palette.dark._900, palette.dark._500),
                    contentColor = Color.White,

                    )
            }
        }
    }
}