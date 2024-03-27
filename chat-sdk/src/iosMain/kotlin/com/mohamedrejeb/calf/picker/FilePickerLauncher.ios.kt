package com.mohamedrejeb.calf.picker

import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.ui.utils.rootViewController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.TemporaryFileURL
import com.mohamedrejeb.calf.io.TemporaryImageURL
import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSItemProvider
import platform.Foundation.NSURL
import platform.Foundation.pathExtension
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationAssetRepresentationModeCurrent
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeApplication
import platform.UniformTypeIdentifiers.UTTypeAudio
import platform.UniformTypeIdentifiers.UTTypeData
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypeText
import platform.UniformTypeIdentifiers.UTTypeVideo
import platform.darwin.NSObject
import kotlin.coroutines.resume

@BetaInteropApi
@Composable
internal actual fun rememberFilePickerLauncher(
    type: FilePickerFileType,
    selectionMode: FilePickerSelectionMode,
    onResult: (List<KmpFile>) -> Unit,
): FilePickerLauncher =
    if (type == FilePickerFileType.Image) {
        rememberImagePickerLauncher(type, selectionMode, onResult)
    } else {
        rememberDocumentPickerLauncher(type, selectionMode, onResult)
    }

@Composable
private fun rememberDocumentPickerLauncher(
    type: FilePickerFileType,
    selectionMode: FilePickerSelectionMode,
    onResult: (List<KmpFile>) -> Unit,
): FilePickerLauncher {
    val coroutineScope = rememberCoroutineScope()

    val delegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentAtURL: NSURL
            ) {
                val tmpUrl = runCatching {
                    TemporaryFileURL(didPickDocumentAtURL).contentURL
                }.getOrNull()

                onResult(listOfNotNull(tmpUrl))
            }

            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val dataList = didPickDocumentsAtURLs.mapNotNull {
                    it as? NSURL
                }.mapNotNull { runCatching { TemporaryFileURL(it).contentURL }.getOrNull() }
                coroutineScope.launch {
                    withContext(Dispatchers.Main) {
                        onResult(dataList)
                    }
                }
            }
        }
    }

    return remember {
        FilePickerLauncher(
            type = type,
            selectionMode = selectionMode,
            onLaunch = {
                val pickerController = createUIDocumentPickerViewController(
                    delegate = delegate,
                    type = type,
                    selectionMode = selectionMode,
                )

                val rootViewController = UIApplication.rootViewController()
                if (rootViewController?.presentedViewController != null) {
                    rootViewController.presentedViewController?.presentViewController(
                        pickerController,
                        true,
                        null
                    )
                } else {
                    rootViewController?.presentViewController(
                        pickerController,
                        true,
                        null
                    )
                }
            }
        )
    }
}

@Composable
private fun rememberImagePickerLauncher(
    type: FilePickerFileType,
    selectionMode: FilePickerSelectionMode,
    onResult: (List<KmpFile>) -> Unit,
): FilePickerLauncher {
    val coroutineScope = rememberCoroutineScope()

    val pickerDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                Monitor.debug("didFinishPicking: $didFinishPicking")

                coroutineScope.launch {
                    val results = didFinishPicking.mapNotNull {
                        val result = it as? PHPickerResult ?: return@mapNotNull null
                        result.itemProvider.loadFileRepresentationForTypeIdentifier()
                    }

                    onResult(results)
                }
            }
        }
    }

    return remember {
        FilePickerLauncher(
            type = type,
            selectionMode = selectionMode,
            onLaunch = {
                val imagePicker = createPHPickerViewController(
                    delegate = pickerDelegate,
                    selectionMode = selectionMode,
                )
                val rootViewController = UIApplication.rootViewController()
                if (rootViewController?.presentedViewController != null) {
                    rootViewController.presentedViewController?.dismissViewControllerAnimated(true, null)

                }
                rootViewController?.presentViewController(
                    imagePicker,
                    true,
                    null
                )
            }
        )
    }
}

private suspend fun NSItemProvider.loadFileRepresentationForTypeIdentifier(): NSURL? =
    suspendCancellableCoroutine { cont ->
        loadFileRepresentationForTypeIdentifier(
            typeIdentifier = UTTypeImage.identifier,
        ) { url, error ->
            if (error != null) {
                Monitor.error("Error: $error")
                cont.resume(null)
            } else {
                Monitor.debug("url=$url, ext=${url?.pathExtension}")
                val tmpUrl = url?.let { TemporaryImageURL(it) }
                val contentUrl = runCatching { tmpUrl?.contentURL }.getOrNull()
                cont.resume(contentUrl)
            }
        }
    }

private fun createUIDocumentPickerViewController(
    delegate: UIDocumentPickerDelegateProtocol,
    type: FilePickerFileType,
    selectionMode: FilePickerSelectionMode,
): UIDocumentPickerViewController {
    val contentTypes = type.value.mapNotNull { mimeType ->
        when (mimeType) {
            FilePickerFileType.ImageContentType -> UTTypeImage
            FilePickerFileType.VideoContentType -> UTTypeVideo
            FilePickerFileType.AudioContentType -> UTTypeAudio
            FilePickerFileType.DocumentContentType -> UTTypeApplication
            FilePickerFileType.TextContentType -> UTTypeText
            FilePickerFileType.AllContentType -> UTTypeData
            FilePickerFileType.FolderContentType -> UTTypeFolder
            else -> UTType.typeWithMIMEType(mimeType)
        }
    }

    val pickerController = UIDocumentPickerViewController(
        forOpeningContentTypes = contentTypes,
        asCopy = type != FilePickerFileType.Folder,
    )
    pickerController.delegate = delegate
    pickerController.allowsMultipleSelection = selectionMode == FilePickerSelectionMode.Multiple
    return pickerController
}

private fun createPHPickerViewController(
    delegate: PHPickerViewControllerDelegateProtocol,
    selectionMode: FilePickerSelectionMode,
): PHPickerViewController {
    val configuration = PHPickerConfiguration(PHPhotoLibrary.sharedPhotoLibrary())
    val newFilter = PHPickerFilter.anyFilterMatchingSubfilters(
        listOf(
            PHPickerFilter.imagesFilter(),
        )
    )
    configuration.filter = newFilter
    configuration.preferredAssetRepresentationMode =
        PHPickerConfigurationAssetRepresentationModeCurrent
    configuration.selection = PHPickerConfigurationSelectionOrdered
    configuration.selectionLimit = if (selectionMode == FilePickerSelectionMode.Multiple) 0 else 1
    configuration.preselectedAssetIdentifiers = listOf<Nothing>()
    val picker = PHPickerViewController(configuration)
    picker.delegate = delegate
    return picker
}

internal actual class FilePickerLauncher actual constructor(
    type: FilePickerFileType,
    selectionMode: FilePickerSelectionMode,
    private val onLaunch: () -> Unit,
) {
    actual fun launch() {
        onLaunch()
    }
}