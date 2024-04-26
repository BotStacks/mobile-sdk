/*
 * Copyright (c) 2023.
 */

@file:OptIn(ExperimentalMaterialApi::class)

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.actions.send
import ai.botstacks.sdk.internal.navigation.BackHandler
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.internal.state.Location
import ai.botstacks.sdk.internal.state.Upload
import ai.botstacks.sdk.internal.ui.components.ActionItemDefaults
import ai.botstacks.sdk.internal.ui.components.GiphyModalSheet
import ai.botstacks.sdk.internal.ui.components.ProgressOverlay
import ai.botstacks.sdk.internal.ui.components.Text
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.ui.components.camera.rememberCameraManager
import ai.botstacks.sdk.internal.ui.components.location.rememberLocationManager
import ai.botstacks.sdk.internal.ui.components.permissions.PermissionCallback
import ai.botstacks.sdk.internal.ui.components.permissions.PermissionStatus
import ai.botstacks.sdk.internal.ui.components.permissions.PermissionType
import ai.botstacks.sdk.internal.ui.components.permissions.createPermissionsManager
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.attachment
import ai.botstacks.sdk.internal.utils.genChat
import ai.botstacks.sdk.internal.utils.imageAttachment
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.state.Message
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch


internal enum class Media {
    pickPhoto,
    pickVideo,
    recordPhoto,
    recordVideo,
    gif,
    file,
    contact,
    location;

    companion object {
        val supportedMediaTypes = listOf(
            pickPhoto,
            recordPhoto,
            gif,
            location
        )
    }
}

/**
 * MediaActionSheetState
 *
 * A state that drives visibility of the [MediaActionSheet].
 */
class MediaActionSheetState(
    internal val chat: Chat,
    val parentMessageId: String? = null,
    sheetState: ModalBottomSheetState? = null
) : ActionSheetState(sheetState) {
    constructor(message: Message,  sheetState: ModalBottomSheetState? = null) : this(message.chat, parentMessageId = message.id, sheetState)
}

/**
 * Creates a [MediaActionSheetState] and remembers it.
 */
@Composable
fun rememberMediaActionSheetState(chat: Chat): MediaActionSheetState {

    val state = ActionSheetDefaults.SheetState

    return remember(chat) { MediaActionSheetState(chat, null, state) }
}

/**
 * Creates a [MediaActionSheetState] and remembers it.
 */
@Composable
fun rememberMediaActionSheetState(message: Message): MediaActionSheetState {

    val state = ActionSheetDefaults.SheetState

    return remember(message) { MediaActionSheetState(message, state) }
}

/**
 * MediaActionSheet
 *
 * A modal bottom sheet that displays attachments that can be sent in a chat. This is a top level
 * scaffold that is designed to wrap your screen content.
 *
 * @param state the state for this action sheet.
 * @param content your screen content.
 *
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MediaActionSheet(
    state: MediaActionSheetState,
    content: @Composable () -> Unit
) {
    MediaActionSheetContainer(state) { onSelection ->
        ModalBottomSheetLayout(
            sheetState = state.sheetState ?: ActionSheetDefaults.SheetState,
            sheetBackgroundColor = BotStacks.colorScheme.background,
            sheetContentColor = BotStacks.colorScheme.onBackground,
            scrimColor = BotStacks.colorScheme.scrim,
            sheetContent = {
                MediaActionSheetContent(onSelection)
            },
            content = content
        )
    }
}

@Composable
internal fun MediaActionSheetContainer(
    state: MediaActionSheetState,
    content: @Composable (onSelection: (Media) -> Unit) -> Unit
) {
    var media by remember { mutableStateOf<Media?>(null) }
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    val hide = {
        media = null
        scope.launch { state.hide() }
        loading = false
    }

    val onFile = { file: KmpFile ->
        op({
            state.chat.send(state.parentMessageId, upload = Upload(file = file))
        })
        media = null
    }
    Box {
        content { media = it }

        if (media != null) {
            when (media) {
                Media.pickPhoto, Media.pickVideo -> {
                    scope.launch { state.hide() }
                    AssetPicker(
                        video = media == Media.pickVideo,
                        onUri = onFile,
                        onCancel = hide
                    )
                }

                Media.recordPhoto, Media.recordVideo -> {
                    scope.launch { state.hide() }
                    CameraPicker(
                        video = media == Media.recordVideo,
                        onUri = onFile,
                        onCancel = hide
                    )
                }

                Media.gif -> {
                    scope.launch { state.hide() }

                    GifPicker(
                        onUri = {
                            hide()
                            state.chat.send(state.parentMessageId, attachments = listOf(it.imageAttachment()))
                        },
                        onCancel = hide
                    )
                }

                Media.contact -> {
//                        ContactPicker(onContact = {
//                            chat.send(null, attachments = listOf(it))
//                            hide()
//                        }) {
//                            hide()
//                        }
                }

                Media.file -> {
                    FilePicker(
                        onUri = {
                            onFile(it)
                        },
                        onCancel = hide
                    )
                }

                Media.location -> {
                    scope.launch { state.hide() }
                    LocationPicker(
                        onLoading = { loading = true },
                        onLocation = {
                            if (loading) {
                                state.chat.send(state.parentMessageId, attachments = listOf(it.attachment()))
                                hide()
                            }
                        },
                        onCancel = hide
                    )
                }

                else -> Monitor.error("empty media")
            }
        }

        ProgressOverlay(loading)
    }
}
@Composable
internal fun MediaActionSheetContent(onSelection: (Media) -> Unit) {
    Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
        Spacer(Modifier.height(8.dp))
        val items = ActionItemDefaults.mediaItems(onSelection)
        items.onEachIndexed { index, item ->
            item()
            if (index != items.lastIndex) {
                Divider(color = BotStacks.colorScheme.caption)
            }
        }
    }
}

@IPreviews
@Composable
private fun MediaActionSheetPreview() {
    BotStacksThemeEngine {
        val composeScope = rememberCoroutineScope()
        val state = rememberMediaActionSheetState(genChat(),)
        MediaActionSheet(state,) {
            Button(onClick = { composeScope.launch { state.show() } }) {
                Text(text = "Open Sheet", fontStyle = BotStacks.fonts.body2)
            }
        }
    }
}

@Composable
internal fun AssetPicker(video: Boolean, onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
    val pickerLauncher = rememberFilePickerLauncher(
        type = if (video) FilePickerFileType.Video else FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            val file = files.firstOrNull()
            Monitor.debug("selected $file")
            file?.let(onUri) ?: onCancel()
        }
    )

    LaunchedEffect(Unit) {
        pickerLauncher.launch()
    }
}

@Composable
internal fun FilePicker(onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Custom(
            listOf(
                FilePickerFileType.DocumentContentType,
                FilePickerFileType.TextContentType,
            ),
        ),
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files -> files.firstOrNull()?.let(onUri) ?: onCancel() }
    )

    LaunchedEffect(Unit) {
        pickerLauncher.launch()
    }
}

@Composable
internal fun CameraPicker(video: Boolean, onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
    val cameraManager = rememberCameraManager { result ->
        if (result != null) {
            onUri(result)
        } else {
            onCancel()
        }
    }

    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus) {
            when (permissionType) {
                PermissionType.Camera -> {
                    if (status == PermissionStatus.GRANTED) {
                        cameraManager.launch()
                    } else {
                        Monitor.error("camera permission not granted")
                        onCancel()
                    }
                }

                else -> Unit
            }
        }
    })

    permissionsManager.askPermission(PermissionType.Camera)
}

@Composable
internal fun LocationPicker(
    onLoading: () -> Unit,
    onLocation: (Location) -> Unit,
    onCancel: () -> Unit
) {
    val locationManager = rememberLocationManager {
        it?.let(onLocation) ?: onCancel()
    }

    val fetchLocation = {
        onLoading()
        locationManager.launch()
    }

    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus) {
            when (permissionType) {
                PermissionType.Location -> {
                    if (status == PermissionStatus.GRANTED) {
                        fetchLocation()
                    } else {
                        Monitor.error("location permission not granted")
                        onCancel()
                    }
                }

                else -> Unit
            }
        }
    })

    permissionsManager.askPermission(PermissionType.Location)
}

//@Composable
//fun ContactPicker(onContact: (AttachmentInput) -> Unit, onCancel: () -> Unit) {
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickContact(),
//        onResult = {
//            it?.let({ contactUriToVCard(it).attachment() })?.let(onContact) ?: onCancel()
//        }
//    )
//    LaunchedEffect(key1 = true, block = { launcher.launch(null) })
//}

@Composable
internal fun GifPicker(onUri: (String) -> Unit, onCancel: () -> Unit) {
    GiphyModalSheet(onSelection = onUri, onCancel = onCancel)
    BackHandler(enabled = true) { onCancel() }
}