package ai.botstacks.sdk.internal.ui.components.location

import ai.botstacks.sdk.internal.state.Location
import ai.botstacks.sdk.ui.utils.IntrinsicWidthUIKitView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMapViewOptions
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.kGMSTypeNormal
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithRequest
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode
import platform.darwin.NSObject

@Composable
internal actual fun MapPin(
    modifier: Modifier,
    contentPadding: PaddingValues,
    location: Location,
    userAvatar: String?,
) {


    var isMapRedrawTriggered by remember { mutableStateOf(false) }

    val delegate = object : NSObject(), GMSMapViewDelegateProtocol {}

    var isMapSetupCompleted by remember { mutableStateOf(false) }

    val imageView = rememberSaveable(userAvatar) {
        UIImageView(frame = CGRectMake(0.0, 0.0, 50.0, 50.0)).apply {
            contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
            layer.borderColor = UIColor.whiteColor.CGColor
            layer.borderWidth = 2.0
            layer.cornerRadius = frame.useContents { size.height / 2 }
            clipsToBounds = true
            layer.zPosition = 1.0
            loadImageFromUrl(url = userAvatar.orEmpty())
        }
    }

    val options = remember { GMSMapViewOptions() }
    val cameraPosition by remember(location) {
        derivedStateOf {
            GMSCameraPosition.cameraWithLatitude(
                latitude = location.latitude ?: 0.0,
                longitude = location.longitude ?: 0.0,
                zoom = 13f
            )
        }
    }
    val googleMapView = remember(options, cameraPosition) {
        GMSMapView(options).apply {
            this.camera = cameraPosition
        }
    }

    LaunchedEffect(location) {
        isMapRedrawTriggered = true
    }
    // Note: `GoogleMaps` using UIKit is a bit of a hack, it's not a real Composable, so we have to
    //       trigger independent updates of the map parts, and sometimes re-render the
    //       map elements. That's why theres all these `is` variables, and the `isRedrawMapTriggered`
    //       variable.
    //       If its not done like this, the UI for the map will not allow the user to move around.
    Box(Modifier.fillMaxSize().width(300.dp)) {
        // Google Maps
        UIKitView(
            modifier = modifier.fillMaxSize(),
            interactive = true,
            factory = {
                googleMapView.delegate = delegate
                return@UIKitView googleMapView
            },
            update = { view ->
                // set the map up only once, this allows the user to move the map around
                if (!isMapSetupCompleted) {
                    view.settings.setAllGesturesEnabled(false)
                    view.settings.setScrollGestures(false)
                    view.settings.setZoomGestures(false)
                    view.settings.setCompassButton(false)

                    view.mapType = kGMSTypeNormal

                    view.myLocationEnabled = false // show the users dot
                    view.settings.myLocationButton = false // we use our own location circle

                    isMapSetupCompleted = true
                }

                if (isMapRedrawTriggered) {
                    view.clear()

                    if (location.latitude != null && location.longitude != null) {
                        val tempMarker = GMSMarker().apply {
                            position = CLLocationCoordinate2DMake(
                                location.latitude, location.longitude
                            )
                            iconView = imageView
                            map = view
                        }
                    }

                    isMapRedrawTriggered = false
                }
            }
        )
    }
}

private fun UIImageView.loadImageFromUrl(url: String) {
    runCatching {
        val request = NSURLRequest.requestWithURL(NSURL(string = url))
        NSURLSession.sharedSession.dataTaskWithRequest(
            request = request,
            completionHandler = { data, response, error ->
                if (error == null && response != null && data != null) {
                    if ((response as NSHTTPURLResponse).statusCode == 200L) {
                        CoroutineScope(Dispatchers.Main).launch {
                            image = UIImage(data = data)
                        }
                    }
                }
            }
        ).resume()
    }
}