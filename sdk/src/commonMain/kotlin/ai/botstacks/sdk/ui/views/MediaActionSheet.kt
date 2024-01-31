/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.views

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.giphy.sdk.core.models.enums.RenditionType
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.themes.GPHTheme
import com.giphy.sdk.ui.utils.imageWithRenditionType
import com.giphy.sdk.ui.views.dialogview.GiphyDialogView
import com.giphy.sdk.ui.views.dialogview.setup
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import ai.botstacks.sdk.actions.imageAttachment
import ai.botstacks.sdk.actions.send
import ai.botstacks.sdk.extensions.contains
import ai.botstacks.sdk.state.*
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.resources.Res
import ai.botstacks.sdk.utils.*
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


enum class Media {
    pickPhoto,
    pickVideo,
    recordPhoto,
    recordVideo,
    gif,
    file,
    contact,
    location
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalResourceApi::class)
fun MediaActionSheet(
    state: ModalBottomSheetState,
    chat: Chat,
    inReplyTo: Message?,
    dismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    var media by remember { mutableStateOf<Media?>(null) }
    val scope = rememberCoroutineScope()
    val hide = {
        media = null
        scope.launch { state.hide() }
    }

    val onFile = { uri: Uri ->
        op({
            chat.send(inReplyTo?.id, upload = Upload(uri = uri))
        })
        hide()
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetBackgroundColor = BotStacks.colorScheme.background,
        sheetContentColor = BotStacks.colorScheme.onBackground,
        scrimColor = BotStacks.colorScheme.caption,
        sheetContent = {
            Box {
                Column {
                    Space(8f)
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.ImageSquare),
                        text = "Upload Photo",
                        divider = false
                    ) {
                        media = Media.pickPhoto
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.Camera),
                        text = "Take Photo",
                        divider = true
                    ) {
                        media = Media.recordPhoto
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.FileVideo),
                        text = "Upload Video",
                        divider = false
                    ) {
                        media = Media.pickVideo
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.VideoCamera),
                        text = "Video Camera",
                        divider = true
                    ) {
                        media = Media.recordVideo
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.Gif),
                        text = "Send a GIF",
                        divider = true
                    ) {
                        media = Media.gif
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.MapPin),
                        text = "Send Location",
                        divider = true
                    ) {
                        media = Media.location
                    }
                    ActionItem(
                        icon = painterResource(Res.Drawables.Outlined.AddressBook),
                        text = "Share Contact",
                        divider = true
                    ) {
                        media = Media.contact
                    }
                }
                when (media) {
                    Media.pickPhoto, Media.pickVideo -> AssetPicker(
                        video = media == Media.pickVideo,
                        onUri = {
                            onFile(it.toUri())
                        }) { hide() }

                    Media.recordPhoto, Media.recordVideo -> CameraPicker(
                        video = media == Media.recordVideo,
                        onUri = {
                            onFile(it)
                        }) { hide() }

                    Media.gif -> GifPicker(onUri = {
                        chat.send(inReplyTo?.id, attachments = listOf(it.toUri().imageAttachment()))
                        hide()
                    }) { hide() }

                    Media.contact -> ContactPicker(onContact = {
                        chat.send(inReplyTo?.id, attachments = listOf(it))
                        hide()
                    }) {
                        hide()
                    }

                    Media.location -> LocationPicker(onLocation = {
                        chat.send(inReplyTo?.id, attachments = listOf(it.attachment()))
                        hide()
                    }) {
                        hide()
                    }

                    else -> Log.d("IAC", "empty media")
                }
            }
        },
        content = content
    )
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
            dismiss = { ctx.launch { open.hide() } },
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
        onResult = { files -> files.firstOrNull() ?: onCancel() }
    )

    LaunchedEffect(video, onUri) {
        pickerLauncher.launch()
    }

}

@Composable
fun CameraPicker(video: Boolean, onUri: (Uri) -> Unit, onCancel: () -> Unit) {
    val uri = LocalContext.current.cacheDir.resolve(uuid()).toUri()
    val launcher = rememberLauncherForActivityResult(
        contract = if (video) ActivityResultContracts.CaptureVideo() else ActivityResultContracts.TakePicture(),
        onResult = {
            if (it) {
                onUri(uri)
            } else {
                onCancel()
            }
        })
    LaunchedEffect(key1 = true, block = {
        launcher.launch(uri)
    })
}

@Composable
fun ContactPicker(onContact: (AttachmentInput) -> Unit, onCancel: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = {
            it?.let({ contactUriToVCard(it).attachment() })?.let(onContact) ?: onCancel()
        }
    )
    LaunchedEffect(key1 = true, block = { launcher.launch(null) })
}

@Composable
fun GifPicker(onUri: (String) -> Unit, onCancel: () -> Unit) {
    var offset by rememberSaveable { mutableStateOf(0f) }
    val configuration = LocalConfiguration.current
    AndroidView(
        factory = { ctx ->
            val settings =
                GPHSettings(theme = GPHTheme.Light, stickerColumnCount = 3)
            settings.mediaTypeConfig = arrayOf(GPHContentType.gif)
            GiphyDialogView(ctx).apply {
                setup(
                    settings
                )
                this.listener = object : GiphyDialogView.Listener {
                    override fun didSearchTerm(term: String) {
                    }

                    override fun onClosed(selectedContentType: GPHContentType) {
                    }

                    override fun onFocusSearch() {
                    }

                    override fun onGifSelected(
                        media: com.giphy.sdk.core.models.Media,
                        searchTerm: String?,
                        selectedContentType: GPHContentType
                    ) {
                        (media.imageWithRenditionType(RenditionType.fixedWidth)?.gifUrl
                            ?: media.contentUrl ?: media.source)?.let {
                            onUri(it)
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        offset += change.positionChange().y
                        if (offset > configuration.screenHeightDp.dp.toPx() * 0.6 || dragAmount > 50) {
                            onCancel()
                        }
                    },
                    onDragEnd = {
                        if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                            offset = 0f
                        }
                    },
                    onDragCancel = {
                        if (offset <= configuration.screenHeightDp.dp.toPx() * 0.6) {
                            offset = 0f
                        }
                    }
                )
            }
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPicker(onLocation: (Location) -> Unit, onCancel: () -> Unit) {
    // Camera permission state
    val state = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    val have = state.allPermissionsGranted || state.permissions.contains { it.status.isGranted }
    val activity = LocalContext.current as Activity
    LaunchedEffect(key1 = have, block = {
        if (have) {
            onCancel()
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            val cancellationToken = CancellationTokenSource()
            val loc = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
            if (loc != null) {
                onLocation(loc)
            } else {
                onCancel()
            }
        } else {
            state.launchMultiplePermissionRequest()
        }
    })
}