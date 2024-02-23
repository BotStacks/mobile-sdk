/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.actions.send
import ai.botstacks.sdk.navigation.BackHandler
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Upload
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.ActionItemDefaults
import ai.botstacks.sdk.ui.components.internal.ProgressOverlay
import ai.botstacks.sdk.ui.components.internal.camera.rememberCameraManager
import ai.botstacks.sdk.ui.components.internal.location.rememberLocationManager
import ai.botstacks.sdk.ui.components.internal.permissions.PermissionCallback
import ai.botstacks.sdk.ui.components.internal.permissions.PermissionStatus
import ai.botstacks.sdk.ui.components.internal.permissions.PermissionType
import ai.botstacks.sdk.ui.components.internal.permissions.createPermissionsManager
import ai.botstacks.sdk.utils.GiphyModalSheet
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.attachment
import ai.botstacks.sdk.utils.genChat
import ai.botstacks.sdk.utils.imageAttachment
import ai.botstacks.sdk.utils.op
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
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
import co.touchlab.kermit.Logger
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch


enum class Media {
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

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MediaActionSheet(
    state: ModalBottomSheetState,
    chat: Chat,
    inReplyTo: Message?,
    content: @Composable () -> Unit
) {
    var media by remember { mutableStateOf<Media?>(null) }
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    val hide = {
        media = null
        scope.launch { state.hide() }
        loading = false
        Unit
    }

    val onFile = { file: KmpFile ->
        op({
            chat.send(inReplyTo?.id, upload = Upload(file = file))
        })
        media = null
    }


    Box {
        ModalBottomSheetLayout(
            sheetState = state,
            sheetBackgroundColor = BotStacks.colorScheme.background,
            sheetContentColor = BotStacks.colorScheme.onBackground,
            scrimColor = BotStacks.colorScheme.scrim,
            sheetContent = {
                Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    Space(8f)
                    val items = ActionItemDefaults.mediaItems { media = it }
                    items.onEachIndexed { index, item ->
                        item()
                        if (index != items.lastIndex) {
                            Divider(color = BotStacks.colorScheme.caption)
                        }
                    }
                }
            },
            content = content
        )

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
                            chat.send(inReplyTo?.id, attachments = listOf(it.imageAttachment()))
                        },
                        onCancel = hide
                    )
                }

                Media.contact -> {
//                        ContactPicker(onContact = {
//                            chat.send(inReplyTo?.id, attachments = listOf(it))
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
                                chat.send(inReplyTo?.id, attachments = listOf(it.attachment()))
                                hide()
                            }
                        },
                        onCancel = hide
                    )
                }

                else -> Logger.d("empty media")
            }
        }

        ProgressOverlay(loading)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@IPreviews
@Composable
fun MediaActionSheetPreview() {
    BotStacksChatContext {
        var open = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val ctx = rememberCoroutineScope()
        MediaActionSheet(
            open,
            genChat(),
            inReplyTo = null
        ) {
            Button(onClick = { ctx.launch { open.show() } }) {
                Text(text = "Open Sheet", fontStyle = BotStacks.fonts.body2)
            }
        }
    }
}

@Composable
fun AssetPicker(video: Boolean, onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
    val pickerLauncher = rememberFilePickerLauncher(
        type = if (video) FilePickerFileType.Video else FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            val file = files.firstOrNull()
            println("selected $file")
            file?.let(onUri) ?: onCancel()
        }
    )

    LaunchedEffect(Unit) {
        pickerLauncher.launch()
    }
}

@Composable
fun FilePicker(onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
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
fun CameraPicker(video: Boolean, onUri: (KmpFile) -> Unit, onCancel: () -> Unit) {
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
                        println("camera permission not granted")
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
private fun LocationPicker(
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
                        println("location permission not granted")
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
fun GifPicker(onUri: (String) -> Unit, onCancel: () -> Unit) {
    GiphyModalSheet(onSelection = onUri, onCancel = onCancel)

    BackHandler(enabled = true) { onCancel() }
}