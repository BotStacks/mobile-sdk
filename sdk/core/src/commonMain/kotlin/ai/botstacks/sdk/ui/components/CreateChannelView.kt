package ai.botstacks.sdk.ui.components

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
import botstacks.sdk.core.generated.resources.Res
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Stable
class CreateChannelState {
    var selectedImage by mutableStateOf<KmpFile?>(null)

    var name by mutableStateOf(TextFieldValue())

    var private by mutableStateOf(false)

    var participants = mutableStateListOf<User>()
    val participantCount get() = participants.count()

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
}

@OptIn(ExperimentalResourceApi::class)
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
                    empty = painterResource(Res.drawable.camera)
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
                    icon = Res.drawable.lock_fill,
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
                UserSelectView(
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