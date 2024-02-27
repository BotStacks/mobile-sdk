package ai.botstacks.sdk.internal.ui.components.permissions

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.internal.utils.contains
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@Composable
internal actual fun createPermissionsManager(callback: PermissionCallback): PermissionsManager {
    return remember { PermissionsManager(callback) }
}

internal actual class PermissionsManager actual constructor(private val callback: PermissionCallback) :
    PermissionHandler {
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun askPermission(permission: PermissionType) {
        val lifecycleOwner = LocalLifecycleOwner.current
        when (permission) {
            is PermissionType.Camera -> {
                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
                LaunchedEffect(cameraPermissionState.status) {
                    val permissionResult = cameraPermissionState.status
                    if (!permissionResult.isGranted) {
                        if (permissionResult.shouldShowRationale) {
                            callback.onPermissionStatus(
                                permission, PermissionStatus.SHOW_RATIONALE
                            )
                        } else {
                            lifecycleOwner.lifecycleScope.launch {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    } else {
                        callback.onPermissionStatus(
                            permission, PermissionStatus.GRANTED
                        )
                    }
                }
            }

            PermissionType.Location -> {
                val permissionState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )

                LaunchedEffect(permissionState.allPermissionsGranted, permissionState.revokedPermissions) {
                    val granted = permissionState.allPermissionsGranted || permissionState.permissions.contains { it.status.isGranted }
                    if (!granted) {
                        if (permissionState.shouldShowRationale) {
                            callback.onPermissionStatus(
                                permission, PermissionStatus.SHOW_RATIONALE
                            )
                        } else {
                            lifecycleOwner.lifecycleScope.launch {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        }
                    } else {
                        callback.onPermissionStatus(
                            permission, PermissionStatus.GRANTED
                        )
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun isPermissionGranted(permission: PermissionType): Boolean {
        return when (permission) {
            is PermissionType.Camera -> {
                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
                cameraPermissionState.status.isGranted
            }

            PermissionType.Location -> {
                val permissionState = rememberMultiplePermissionsState(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                permissionState.allPermissionsGranted || permissionState.permissions.contains { it.status.isGranted }
            }
        }
    }

    @Composable
    override fun launchSettings() {
        val context = LocalContext.current
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", BotStacksChat.shared.appIdentifier, null)
        ).also {
            context.startActivity(it)
        }
    }
}