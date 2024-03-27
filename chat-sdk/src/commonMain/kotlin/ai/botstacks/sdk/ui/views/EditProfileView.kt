package ai.botstacks.sdk.ui.views

import ai.botstacks.`chat-sdk`.generated.resources.Res
import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.internal.ui.components.TextInput
import ai.botstacks.sdk.internal.state.Upload
import ai.botstacks.sdk.internal.utils.opbg
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.type.UpdateProfileInput
import ai.botstacks.sdk.ui.components.Avatar
import ai.botstacks.sdk.ui.components.AvatarSize
import ai.botstacks.sdk.ui.components.AvatarType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import com.apollographql.apollo3.api.Optional
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import dev.icerock.moko.resources.compose.painterResource

/**
 * EditProfileState
 *
 * State holder for editing the current user, used specifically within the [EditProfileView].
 *
 * To trigger an update once modifications are done within the view, simply call [update].
 * This will return a Result with the updated User if successful, or the error if it fails. While the
 * create action is waiting for a result [saving] will be true.
 *
 * Resulting changes are persisted internally and do not need to be saved manually.
 *
 */
@Stable
class EditProfileState {

    private val currentUser = User.current!!

    internal var selectedImage by mutableStateOf<KmpFile?>(null)
    internal var textState by mutableStateOf(TextFieldValue(currentUser.username))


    internal val userImage: Any?
        get() = selectedImage ?: currentUser.avatar


    var saving by mutableStateOf(false)
        private set

    suspend fun update(): Result<User?> {
        val uploadFile = selectedImage?.let { Upload(file = it) }
        val url = uploadFile?.await()
        return runCatching {
            API.updateProfile(
                UpdateProfileInput(
                    username = Optional.present(textState.text),
                    image = Optional.presentIfNotNull(url)
                )
            )

            User.current?.apply {
                if (url != null) {
                    avatar = url
                }
                username = textState.text
            }
        }.onFailure { saving = false }.onSuccess { saving = false }
    }

    fun update(onSuccess: (User?) -> Unit, onError: (Throwable) -> Unit) {
        opbg {
            update()
                .onSuccess(onSuccess)
                .onFailure(onError)
        }
    }
}

/**
 * EditProfileView
 *
 * A screen content view for editing the current user.
 *
 * @param state The state for the view
 *
 */
@Composable
fun EditProfileView(
    state: EditProfileState,
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
        contentPadding = PaddingValues(BotStacks.dimens.inset),
    ) {
        item {
            Avatar(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { pickerLauncher.launch() },
                type = AvatarType.User(
                    url = state.userImage,
                    empty = painterResource(Res.images.camera)
                ),
                size = AvatarSize.Large
            )
        }
        item {
            TextInput(
                Modifier
                    .fillMaxWidth()
                    .padding(top = BotStacks.dimens.grid.x6),
                value = state.textState,
                onValueChanged = { state.textState = it },
                placeholder = "Username",
                maxLines = 1,
                fontStyle = BotStacks.fonts.body2,
                indicatorColor = BotStacks.colorScheme.caption,
            )
        }
    }
}