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
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.NSObject

@Composable
actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return PermissionsManager(callback)
}

actual class PermissionsManager actual constructor(private val callback: PermissionCallback) :
    PermissionHandler {

    private val locationManager = CLLocationManager()

    @Composable
    override fun askPermission(permission: PermissionType) {
        when (permission) {
            is PermissionType.Camera -> {
                val status: AVAuthorizationStatus =
                    remember { AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) }
                askCameraPermission(status, permission, callback)
            }

            PermissionType.Location -> {
                askLocationPermission(permission, locationManager, callback)
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

            PermissionType.Location -> {
                val authStatus = locationManager.authorizationStatus()
                println("location auth status=$authStatus")
                authStatus == 4 || authStatus == 3
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

    private fun askLocationPermission(
        permission: PermissionType,
        locationManager: CLLocationManager,
        callback: PermissionCallback
    ) {
        when (val authStatus = locationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                CoroutineScope(Dispatchers.Main).launch {
                    callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                }
            }

            kCLAuthorizationStatusNotDetermined -> {
                locationManager.requestWhenInUseAuthorization()
                locationManager.delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val isGranted = when (didChangeAuthorizationStatus) {
                                kCLAuthorizationStatusAuthorizedWhenInUse,
                                kCLAuthorizationStatusAuthorizedAlways -> true
                                else -> false
                            }

                            if (isGranted) {
                                callback.onPermissionStatus(permission, PermissionStatus.GRANTED)
                            } else {
                                callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                            }
                        }
                    }
                }
            }

            kCLAuthorizationStatusDenied -> {
                CoroutineScope(Dispatchers.Main).launch {
                    callback.onPermissionStatus(permission, PermissionStatus.DENIED)
                }
            }

            else -> error("unknown location status $authStatus")
        }
    }
}