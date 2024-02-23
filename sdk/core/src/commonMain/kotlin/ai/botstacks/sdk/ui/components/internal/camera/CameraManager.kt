package ai.botstacks.sdk.ui.components.internal.camera

import androidx.compose.runtime.Composable
import com.mohamedrejeb.calf.io.KmpFile


@Composable
expect fun rememberCameraManager(onResult: (KmpFile?) -> Unit): CameraManager


expect class CameraManager(
    onLaunch: () -> Unit
) {
    fun launch()
}