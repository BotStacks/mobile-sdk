package ai.botstacks.sdk

import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.utils.Giphy
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.op
import ai.botstacks.sdk.utils.readPlist
import ai.botstacks.sdk.utils.retryIO
import androidx.compose.runtime.Stable
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@Stable
actual class BotStacksChatPlatform : BotStacksChat() {

    companion object {
        private val TAG = "BotStacksIOS"
    }


    private lateinit var _apiKey: String
    private lateinit var bundleIdentifier: String

    actual val apiKey: String
        get() = _apiKey

    actual val appIdentifier: String
        get() = bundleIdentifier

    actual val scope = CoroutineScope(Dispatchers.Main)

    override val prefs: Settings
        get() = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults())

    fun setup(apiKey: String) {
        setup(apiKey, null, null, false)
    }

    fun setup(apiKey: String, giphyApiKey: String?, googleMapsApiKey: String?) {
        setup(
            apiKey = apiKey,
            giphyApiKey = giphyApiKey,
            googleMapsApiKey = googleMapsApiKey,
            delayLoad = false
        )
    }

    fun setup(apiKey: String,
              giphyApiKey: String?,
              googleMapsApiKey: String?,
              delayLoad: Boolean = false
    ) {
        this._apiKey = apiKey
        this.bundleIdentifier = NSBundle.mainBundle.bundleIdentifier().orEmpty()

        BotStacksChatStore.current.init()
        BotStacksChatStore.current.contacts.requestContacts = false

        if (!giphyApiKey.isNullOrEmpty()) {
            Giphy.configureWithApiKey(giphyApiKey, verificationMode = false, metadata = emptyMap<Any?, Any>())
            hasGiphySupport = true
        }

        if (!googleMapsApiKey.isNullOrEmpty()) {
            hasMapsSupport = true
        }

        hasLocationSupport = readPlist<String>("Info", "NSLocationAlwaysAndWhenInUseUsageDescription") != null
        // Info.plist privacy reason also required

        // Info.plist privacy reason also required
        hasCameraSupport = readPlist<String>("Info", "NSCameraUsageDescription") != null

        if (!delayLoad) {
//            Log.v(TAG, "Launch load")
            scope.launch {
//                Log.v(TAG, "Launching load in bg")
                op({
                    bg { load() }
                }) {

                }
            }
        }
    }

    private var didStartLoading = false

    actual suspend fun load() {
        if (apiKey.isEmpty()) {
            throw Error("You must initialize BotStacksChat with BotStacksChat.init before calling load")
        }
        if (didStartLoading) throw Error("SDK Already initialized")
        didStartLoading = true
        retryIO {
            BotStacksChatStore.current.loadAsync()
            isUserLoggedIn = BotStacksChatStore.current.currentUserID != null
            loaded = true
        }
    }
}