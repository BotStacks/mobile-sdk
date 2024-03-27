package ai.botstacks.sdk.ui.views

import ai.botstacks.`chat-sdk`.generated.resources.Res
import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.internal.state.Upload
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.ui.components.TextInput
import ai.botstacks.sdk.internal.ui.components.ToggleSwitch
import ai.botstacks.sdk.internal.ui.components.settings.SettingsSection
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.opbg
import ai.botstacks.sdk.ui.components.Avatar
import ai.botstacks.sdk.ui.components.AvatarSize
import ai.botstacks.sdk.ui.components.AvatarType
import ai.botstacks.sdk.ui.components.UserSelect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import dev.icerock.moko.resources.compose.painterResource

/**
 * CreateChannelState
 *
 * State holder for creating a channel, used specifically within the [CreateChannelView].
 *
 * To trigger a channel creation once modifications are done within the view, simply call [create].
 * This will return a Result with the created chat if successful, or the error if it fails. While the
 * create action is waiting for a result [saving] will be true.
 *
 * Resulting changes are persisted internally and do not need to be saved manually.
 *
 */
@Stable
class CreateChannelState {
    internal var selectedImage by mutableStateOf<KmpFile?>(null)

    internal var name by mutableStateOf(TextFieldValue())

    internal var private by mutableStateOf(false)

    internal var participants = mutableStateListOf<User>()
    internal val participantCount get() = participants.count()

    var saving by mutableStateOf(false)
        private set

    suspend fun create(): Result<Chat?> {
        return bg {
            saving = true
            runCatching {
                val imageUrl = selectedImage?.let { Upload(file = it) }?.await()
                API.createChat(
                    name = name.text,
                    _private = private,
                    image = imageUrl,
                    invites = participants.map { it.id }.filterNot { it == User.current?.id }
                )
            }.onFailure {
                saving = false
            }.onSuccess {
                saving = false
            }
        }
    }

    fun create(onSuccess: (Chat?) -> Unit, onError: (Throwable) -> Unit) {
        opbg {
            create()
                .onSuccess(onSuccess)
                .onFailure(onError)
        }
    }
}

/**
 * CreateChannelView
 *
 * A screen content view for creating a new Channel.
 *
 * @param state The state for the view
 * @param onSelectUsers Callback when the add users icon button is clicked within the User select component. Use this to navigate to a new screen
 * where a user will select participants for this new channel
 *
 * @see [SelectChannelUsersView]
 *
 */
@Composable
fun CreateChannelView(
    state: CreateChannelState,
    onSelectUsers: () -> Unit,
) {
    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            state.selectedImage = files.firstOrNull()
        }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = BotStacks.dimens.inset),
    ) {
        item {
            Avatar(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { pickerLauncher.launch() },
                type = AvatarType.User(
                    url = state.selectedImage,
                    empty = painterResource(Res.images.camera)
                ),
                size = AvatarSize.Large
            )
        }
        item {
            TextInput(
                Modifier
                    .fillMaxWidth()
                    .padding(top = BotStacks.dimens.grid.x6)
                    .padding(horizontal = BotStacks.dimens.inset),
                value = state.name,
                onValueChanged = { state.name = it },
                placeholder = "Enter channel name",
                maxLines = 1,
                fontStyle = BotStacks.fonts.body2,
                indicatorColor = BotStacks.colorScheme.caption,
            )
        }
        item {
            SettingsSection(
                modifier = Modifier.fillMaxWidth()
            ) {
                item(
                    icon = Res.images.lock_fill,
                    title = "Private channel",
                    endSlot = {
                        ToggleSwitch(checked = state.private, onCheckedChange = null)
                    },
                    onClick = { state.private = !state.private }
                )
                divider()
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BotStacks.dimens.inset)
                    .padding(top = BotStacks.dimens.inset),
                verticalArrangement = Arrangement.spacedBy(BotStacks.dimens.inset)
            ) {
                Text("Participant: ${state.participantCount}", fontStyle = BotStacks.fonts.label2)
                UserSelect(
                    modifier = Modifier.fillMaxWidth(),
                    selectedUsers = state.participants,
                    onAddSelected = onSelectUsers,
                    onRemove = { state.participants.remove(it) },
                    canRemove = true,
                )
            }
        }
    }
}