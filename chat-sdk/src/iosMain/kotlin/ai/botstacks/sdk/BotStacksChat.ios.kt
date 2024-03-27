package ai.botstacks.sdk

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.internal.utils.readPlist
import ai.botstacks.sdk.internal.utils.retryIO
import androidx.compose.runtime.Stable
import cocoapods.Giphy.Giphy
import cocoapods.GoogleMaps.GMSServices
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

/**
 * Main iOS entry point for the BotStacks SDK.
 *
 * Setup/initialization is done via [setup], while login and log out are done
 * via [login] and [BotStacksChat.logout], respectively.
 *
 * Registering an FCM token for push notification support is done
 * via [BotStacksChat.registerFCMToken].
 */
@Stable
actual class BotStacksChatPlatform : BotStacksChat() {

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

        Monitor.debug("apikey=$_apiKey, app=$bundleIdentifier")
        BotStacksChatStore.current.init()
        BotStacksChatStore.current.contacts.requestContacts = false

        if (!giphyApiKey.isNullOrEmpty()) {
            Giphy.configureWithApiKey(giphyApiKey, verificationMode = false, metadata = emptyMap<Any?, Any>())
            hasGiphySupport = true
        }

        if (!googleMapsApiKey.isNullOrEmpty()) {
            Monitor.debug("Enabling maps support")
            GMSServices.provideAPIKey(googleMapsApiKey)
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

    /**
    * login to BotStacks Backend
    *
    * @param userId userId of user to associate session with
    * @param username username for user
    * @param displayName optional display name for user
    * @param picture optional user image (avatar) URL
    */
    actual suspend fun login(
        userId: String,
        username: String,
        displayName: String?,
        picture: String?
    ) {
        if (loggingIn) return
        loggingIn = true
        Monitor.debug("Logging in")
        runCatching {
            API.login(
                accessToken = apiKey,
                userId = userId,
                username = username,
                displayName = displayName,
                picture = picture
            )
        }.onSuccess {
            isUserLoggedIn = BotStacksChatStore.current.currentUserID != null
            loggingIn = false
            Monitor.debug("Logged in successfully")
        }.onFailure { err ->
            Monitor.error(err)
            loggingIn = false
        }
    }

    private var didStartLoading = false

    actual suspend fun load() {
        if (apiKey.isEmpty()) {
            throw Error("You must initialize BotStacksChat with BotStacksChat.init before calling load")
        }
        if (didStartLoading) throw Error("SDK Already initialized")
        didStartLoading = true
        retryIO(times = 3) {
            BotStacksChatStore.current.loadAsync()
            isUserLoggedIn = BotStacksChatStore.current.currentUserID != null
            loaded = true
        }
    }
}