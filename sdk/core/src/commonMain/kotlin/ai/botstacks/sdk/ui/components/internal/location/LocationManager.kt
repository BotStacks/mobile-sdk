package ai.botstacks.sdk.ui.components.internal.location

import ai.botstacks.sdk.state.Location
import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationManager(onResult: (Location?) -> Unit): LocationManager


expect class LocationManager(
    onLaunch: suspend () -> Unit
) {
    fun launch()
}