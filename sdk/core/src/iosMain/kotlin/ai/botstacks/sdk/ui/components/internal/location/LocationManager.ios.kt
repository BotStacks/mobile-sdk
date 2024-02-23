package ai.botstacks.sdk.ui.components.internal.location

import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.utils.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
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

object NSLocationStore {
    var latestLocation = AtomicReference<Location?>(null)
        private set
}

@Composable
actual fun rememberLocationManager(onResult: (Location?) -> Unit): LocationManager {
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
        Logger.w { "Error: ${didFailWithError.localizedFailureReason} ${didFailWithError.localizedDescription}, ${didFailWithError.localizedRecoverySuggestion}" }
        Logger.w { "Error: ${didFailWithError.userInfo["timestamp"]}" }
        onLocationUpdate?.invoke(null)
    }

    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
        Logger.w { "Authorization status changed to: $didChangeAuthorizationStatus" }
    }

    override fun locationManagerDidPauseLocationUpdates(manager: CLLocationManager) {
        Logger.w { "locationManagerDidPauseLocationUpdates" }
    }

    override fun locationManagerDidResumeLocationUpdates(manager: CLLocationManager) {
        Logger.w { "locationManagerDidResumeLocationUpdates" }
    }

}

actual class LocationManager actual constructor(
    private val onLaunch: suspend () -> Unit
) {
    actual fun launch() = launch(Dispatchers.Default) {
        onLaunch()
    }
}