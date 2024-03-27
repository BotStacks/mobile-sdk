package ai.botstacks.sdk.internal.ui.components.camera

import ai.botstacks.sdk.internal.ui.utils.rootViewController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.TemporaryImageURL
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraCaptureMode
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

@Composable
internal actual fun rememberCameraManager(onResult: (KmpFile?) -> Unit): CameraManager {
    val imagePicker = UIImagePickerController()
    val cameraDelegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {
            override fun imagePickerController(
                picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image =
                    didFinishPickingMediaWithInfo.getValue(UIImagePickerControllerEditedImage) as? UIImage
                        ?: didFinishPickingMediaWithInfo.getValue(
                            UIImagePickerControllerOriginalImage
                        ) as? UIImage


                val managedFile = image?.let { TemporaryImageURL(image) }
                onResult.invoke(managedFile?.contentURL)
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }
    return remember {
        CameraManager {
            imagePicker.setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
            imagePicker.setAllowsEditing(true)
            imagePicker.setCameraCaptureMode(UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto)
            imagePicker.setDelegate(cameraDelegate)

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
    }
}

internal actual class CameraManager actual constructor(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}