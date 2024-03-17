package ai.botstacks.sdk

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.Monitor
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.utils.bg
import ai.botstacks.sdk.internal.utils.op
import ai.botstacks.sdk.internal.utils.retryIO
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import com.giphy.sdk.ui.Giphy
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Android entry point for the BotStacks SDK.
 *
 * Setup/initialization is done via [setup], while login and log out are done
 * via [login] and [BotStacksChat.logout], respectively.
 *
 * Registering an FCM token for push notification support is done
 * via [BotStacksChat.registerFCMToken].
 */
@Stable
actual class BotStacksChatPlatform : BotStacksChat() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var _apiKey: String
    private lateinit var packageName: String

    actual val apiKey: String
        get() = _apiKey

    actual val appIdentifier: String
        get() = packageName

    override val prefs: Settings
        get() = SharedPreferencesSettings(sharedPreferences)

    actual val scope = CoroutineScope(Dispatchers.Main)

    /**
     * setup app instance for interfacing with the BotStacksSDK.
     *
     * @param context Application context
     * @param apiKey BotStacks API key
     * @param giphyApiKey optional API from Giphy for Gif selection support.
     * @param delayLoad If enabled, you must call [load] prior to rendering UI.
     */
    fun setup(
        context: Context,
        apiKey: String,
        giphyApiKey: String? = null,
        delayLoad: Boolean = false
    ) {
        this.sharedPreferences = context.getSharedPreferences("botstackschat", Context.MODE_PRIVATE)
        this._apiKey = apiKey
        this.packageName = context.packageName

        BotStacksChatStore.current.init()
        BotStacksChatStore.current.contacts.requestContacts =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED

        if (!giphyApiKey.isNullOrEmpty()) {
            Giphy.configure(context, giphyApiKey)
            hasGiphySupport = true
        }

        // retrieve google maps key from merged manifest
        // if present, enable map rendering support
        val googleMapsApiKey = context.packageManager.getApplicationInfo(
            appIdentifier,PackageManager.GET_META_DATA)
            .metaData.getString("com.google.android.geo.API_KEY")

        if (!googleMapsApiKey.isNullOrEmpty()) {
            hasMapsSupport = true // runtime permission grants location access
        }

        hasLocationSupport = true // runtime permission grants camera access
        hasCameraSupport = true // runtime permission grants camera access

        if (!delayLoad) {
            scope.launch {
                op({ bg { load() } }) {}
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
        }.onFailure { err ->
            Monitor.error(err)
            loggingIn = false
        }
    }

    private var didStartLoading = false

    /**
     * Load trigger for loading user data. A logged in check is done during this phase to allow routing
     * to login if needed.
     *
     * NOTE: An [apiKey] must be provided during [setup] prior to calling this.
     */
    actual suspend fun load() {
        Monitor.debug("Start load")
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