package ai.botstacks.sdk.ui.components.internal.location

import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.ui.theme.LocalBotStacksColorPalette
import ai.botstacks.sdk.ui.utils.CircleCropTransformation
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.request.ImageRequest
import coil3.request.transformations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


@Composable
actual fun MapPin(
    modifier: Modifier,
    contentPadding: PaddingValues,
    location: Location,
    userAvatar: String?,
) {
    val currentLocation by remember(location) {
        derivedStateOf {
            val lat = location.latitude ?: return@derivedStateOf null
            val long = location.longitude ?: return@derivedStateOf null
            LatLng(lat, long)
        }
    }

    var bitmap by rememberSaveable {
        mutableStateOf<Bitmap?>(null)
    }

    var render by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (render) {
            // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
            // with input from Compose UI, those updates are encapsulated into the MapViewContainer
            // composable. In this way, when an update to the MapView happens, this composable won't
            // recompose and the MapView won't need to be recreated.
            val mapView = rememberMapViewWithLifecycle()
            var googleMap by remember {
                mutableStateOf<GoogleMap?>(null)
            }
            AndroidView(modifier = Modifier.fillMaxSize(), factory = { mapView }) { view ->
                view.getMapAsync { map ->
                    googleMap = map
                    currentLocation?.let { latLng ->
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                    }
                }
            }

            LaunchedEffect(bitmap, googleMap, currentLocation) {
                googleMap?.let {
                    currentLocation?.let { latLng ->
                        bitmap?.let { image ->
                            it.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(image))
                            )
                        }
                    }
                }
            }
        }
    }

    val palette = LocalBotStacksColorPalette.current
    val borderColor = palette.dayNight._900
    val context = LocalContext.current
    val size: Int = with(LocalDensity.current) { 50.dp.roundToPx() }
    val borderWidth: Int = with(LocalDensity.current) { 2.dp.roundToPx() }

    LaunchedEffect(Unit) {
        delay(100)
        render = true
        bitmap = getBitmapFromURL(context, userAvatar, size, borderColor.toArgb(), borderWidth)
    }
}

@OptIn(ExperimentalCoilApi::class)
private suspend fun getBitmapFromURL(
    context: Context, src: String?,
    size: Int,
    borderColor: Int,
    borderWidth: Int,
): Bitmap? = suspendCancellableCoroutine {
        val imageRequest = ImageRequest.Builder(context)
            .data(src)
            .size(size)
            .transformations(CircleCropTransformation(strokeConfig = CircleCropTransformation.Stroke(borderWidth.toFloat(), borderColor)))
            .target { drawable ->
                val bitmap = drawable.asDrawable(context.resources).toBitmap()
                it.resume(bitmap)
            }.listener(onError = { _, result ->
                result.throwable.printStackTrace()
                it.resume(null)
            })
            .build()
        ImageLoader(context).enqueue(imageRequest)
    }

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = 1
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }