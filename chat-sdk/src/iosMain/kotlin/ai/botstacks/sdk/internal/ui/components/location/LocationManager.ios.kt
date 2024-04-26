package ai.botstacks.sdk.internal.ui.components.location

import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.state.Location
import ai.botstacks.sdk.internal.utils.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.concurrent.AtomicReference

internal object NSLocationStore {
    var latestLocation = AtomicReference<Location?>(null)
        private set
}

@Composable
internal actual fun rememberLocationManager(onResult: (Location?) -> Unit): LocationManager {
    val oneTimeLocationManager = remember { CLLocationManager() }
    val locationDelegate = remember { LocationDelegate() }

    return remember {
        LocationManager {
            oneTimeLocationManager.desiredAccuracy = kCLLocationAccuracyBest

            oneTimeLocationManager.startUpdatingLocation()

            // Define a callback to receive location updates
            locationDelegate.onLocationUpdate = { location ->
                oneTimeLocationManager.stopUpdatingLocation()
                NSLocationStore.latestLocation.value = location

                onResult(location)
            }
            oneTimeLocationManager.delegate = locationDelegate
        }
    }
}

private class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {

    // Define a callback to receive location updates
    var onLocationUpdate: ((Location?) -> Unit)? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        didUpdateLocations.firstOrNull()?.let {
            val location = it as CLLocation
            location.coordinate.useContents {
                onLocationUpdate?.invoke(Location(latitude, longitude))
            }

        }
    }

    override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        Monitor.error("Error: ${didFailWithError.localizedFailureReason} ${didFailWithError.localizedDescription}, ${didFailWithError.localizedRecoverySuggestion}")
        Monitor.error("Error: ${didFailWithError.userInfo["timestamp"]}")
        onLocationUpdate?.invoke(null)
    }

    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
        Monitor.debug("Authorization status changed to: $didChangeAuthorizationStatus")
    }

    override fun locationManagerDidPauseLocationUpdates(manager: CLLocationManager) {
        Monitor.debug("locationManagerDidPauseLocationUpdates")
    }

    override fun locationManagerDidResumeLocationUpdates(manager: CLLocationManager) {
        Monitor.debug("locationManagerDidResumeLocationUpdates")
    }

}

internal actual class LocationManager actual constructor(
    private val onLaunch: suspend () -> Unit
) {
    actual fun launch() = launch(Dispatchers.Default) {
        onLaunch()
    }
}