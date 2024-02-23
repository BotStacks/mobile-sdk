@file:OptIn(ExperimentalFoundationApi::class)

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.API
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Upload
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.internal.EditableTextLabel
import ai.botstacks.sdk.ui.components.internal.ToggleSwitch
import ai.botstacks.sdk.ui.components.internal.settings.SettingsSection
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.readBytes
import ai.botstacks.sdk.utils.ui.onEnter
import ai.botstacks.sdk.utils.ui.unboundedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalFoundationApi::class)
@Stable
class ChannelSettingsState(private val chat: Chat) {
    var selectedImage by mutableStateOf<KmpFile?>(null)

    val channelImage: Any?
        @Composable get() = selectedImage?.readBytes() ?: chat.displayImage

    @OptIn(ExperimentalFoundationApi::class)
    var name by mutableStateOf(TextFieldValue(chat.displayName))
    var isEditingName by mutableStateOf(false)

    var muted: Boolean by mutableStateOf(chat.notification_setting == NotificationSetting.none)

    var private by mutableStateOf(chat._private)

    var participants = mutableStateListOf<User>()
    val participantCount get() = participants.count()

    val sortedParticipants
        get() = participants.sortedWith(
            compareBy<User> { !it.isCurrent }.thenBy { !admins.contains(it.id) })

    val admins get() = chat.admins.map { it.user.id }

    var saving by mutableStateOf(false)
        private set

    init {
        participants.addAll(chat.members.map { it.user })
    }

    fun toggleMute() {
        muted = !muted
    }

    suspend fun update(): Result<Chat?> {
        return bg {
            saving = true
            runCatching {
                val imageUrl = selectedImage?.let { Upload(file = it) }?.await()

                API.updateChat(
                    id = chat.id,
                    name = name.text.toString(),
                    private = private,
                    image = imageUrl,
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
fun ChannelSettingsView(
    state: ChannelSettingsState,
    onOpenAnnouncements: () -> Unit,
    onAddUsers: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(top = BotStacks.dimens.inset),
        verticalArrangement = Arrangement.spacedBy(BotStacks.dimens.inset),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pickerLauncher = rememberFilePickerLauncher(
            type = FilePickerFileType.Image,
            selectionMode = FilePickerSelectionMode.Single,
            onResult = { files ->
                state.selectedImage = files.firstOrNull()
            }
        )

        Avatar(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { pickerLauncher.launch() },
            type = AvatarType.User(
                url = state.channelImage,
                empty = painterResource(Res.drawable.camera)
            ),
            size = AvatarSize.Large
        )

        EditableTextLabel(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = BotStacks.dimens.inset)
                .onEnter { state.isEditingName = false },
            isEditing = state.isEditingName,
            value = state.name,
            onValueChanged = { state.name = it },
            placeholder = "Enter channel name",
            onStartEditing = { state.isEditingName = true },
            onEditComplete = { state.isEditingName = false }
        )

        SettingsSection(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // TODO: reenable once schema supports this
//            item(
//                icon = Res.Drawables.Filled.Speaker,
//                title = "Announcements",
//                onClick = { onOpenAnnouncements() }
//            )
            // TODO: reenable once schema supports this
//            item(
//                icon = Res.Drawables.Filled.BellSimple,
//                title = "Notifications",
//                endSlot = {
//                    ToggleSwitch(checked = state.muted, onCheckedChange = null)
//                },
//                onClick = { state.toggleMute() }
//            )
            item(
                icon = Res.drawable.lock_fill,
                title = "Private channel",
                endSlot = {
                    ToggleSwitch(checked = state.private, onCheckedChange = null)
                },
                onClick = { state.private = !state.private }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .navigationBarsPadding()
                .padding(horizontal = BotStacks.dimens.inset)
                .border(
                    BotStacks.dimens.border,
                    color = BotStacks.colorScheme.border,
                    shape = BotStacks.shapes.medium
                ),
        ) {
            stickyHeader {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(BotStacks.colorScheme.background)
                            .padding(horizontal = BotStacks.dimens.inset)
                            .padding(vertical = BotStacks.dimens.inset),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x3)
                    ) {
                        Icon(
                            modifier = Modifier.size(BotStacks.dimens.staticGrid.x6),
                            painter = painterResource(Res.drawable.users_fill),
                            contentDescription = null
                        )
                        Text(
                            "${state.participantCount} People",
                            fontStyle = BotStacks.fonts.caption1
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.unboundedClickable { onAddUsers() },
                            painter = painterResource(Res.drawable.add_user),
                            contentDescription = "Add user"
                        )
                        // TODO: add search
                    }
                    Divider(color = BotStacks.colorScheme.border)
                }
            }

            items(state.sortedParticipants) { user ->
                UserRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = BotStacks.dimens.inset),
                    avatar = {
                        Avatar(
                            user = user,
                            showOnlineStatus = false,
                        )
                    },
                    displayName = user.displayNameFb,
                    endSlot = {
                        when {
                            user.isCurrent -> {
                                Text("You", fontStyle = BotStacks.fonts.caption1)
                            }

                            state.admins.contains(user.id) -> {
                                Badge(label = "Admin", fontStyle = BotStacks.fonts.caption1)
                            }
                        }
                    }
                )
            }
        }
    }
}