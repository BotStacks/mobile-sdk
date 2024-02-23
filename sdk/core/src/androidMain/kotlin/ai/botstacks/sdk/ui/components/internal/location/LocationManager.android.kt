package ai.botstacks.sdk.ui.components.internal.location

import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.utils.launch
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
actual fun rememberLocationManager(onResult: (Location?) -> Unit): LocationManager {
    val activity = LocalContext.current as Activity

    val locationClient = rememberFusedLocationProviderClient(activity)
    val cancellationToken = CancellationTokenSource()


    return remember {
        LocationManager {
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()

            onResult(location?.let { Location(latitude = it.latitude, longitude = it.longitude) })
        }
    }
}

@Composable
private fun rememberFusedLocationProviderClient(activity: Activity): FusedLocationProviderClient {
    return remember(activity) {
        LocationServices.getFusedLocationProviderClient(activity)
    }
}

actual class LocationManager actual constructor(
    private val onLaunch: suspend () -> Unit
) {
    actual fun launch() = launch(Dispatchers.IO) {
        onLaunch()
    }
}