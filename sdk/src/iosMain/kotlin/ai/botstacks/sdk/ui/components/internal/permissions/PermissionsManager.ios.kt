package ai.botstacks.sdk.ui.components.internal.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return PermissionsManager(callback)
}

actual class PermissionsManager actual constructor(private val callback: PermissionCallback) :
    PermissionHandler {
    @Composable
    override fun askPermission(permission: PermissionType) {
        when (permission) {
            is PermissionType.Camera -> {
                val status: AVAuthorizationStatus =
                    remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                askCameraPermission(status, permission, callback)
            }
        }
    }

    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            is PermissionType.Camera -> {
                val status: AVAuthorizationStatus =
                    remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                status == AVAuthorizationStatusAuthorized
            }
        }
    }

    @Composable
    override fun launchSettings() {
        NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
            UIApplication.sharedApplication.openURL(it)
        }
    }

    private fun askCameraPermission(
        status: AVAuthorizationStatus, permission: PermissionType, callback: PermissionCallback
    ) {
        when (status) {
            AVAuthorizationStatusAuthorized -> {
                CoroutineScope(Dispatchers.Main).launch {
                    callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                }
            }

            AVAuthorizationStatusNotDetermined -> {
                return AVCaptureDevice.Companion.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (isGranted) {
                            callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                        } else {
                            callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                        }
                    }
                }
            }

            AVAuthorizationStatusDenied -> {
                CoroutineScope(Dispatchers.Main).launch {
                    callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                }
            }

            else -> error("unknown camera status $status")
        }
    }
}