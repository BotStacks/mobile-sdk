/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk

import ai.botstacks.sdk.internal.API
import ai.botstacks.sdk.internal.Monitoring
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.utils.async
import ai.botstacks.sdk.internal.utils.opbg
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope

abstract class BotStacksChat {
    /**
     * Whether or not the SDK has loaded initial data
     */
    var loaded by mutableStateOf(false)
        internal set

    /**
     * If currently logging in a user
     */
    var loggingIn by mutableStateOf(false)
        internal set

    /**
     * If a user is currently logged in.
     */
    var isUserLoggedIn by mutableStateOf(false)
        internal set

    internal abstract val prefs: Settings

    fun setupLogging(level: LogLevel, log: (String) -> Unit) {
        monitoring = Monitoring(level, log)
    }

    fun disableLogging() {
        monitoring = Monitoring(level = LogLevel.NONE)
    }

    /**
     * Register a callback for handling log out events
     */
    var onLogout: (() -> Unit)? = null

    internal var hasGiphySupport by mutableStateOf(false)
    internal var hasMapsSupport by mutableStateOf(false)
    internal var hasLocationSupport by mutableStateOf(false)
    internal var hasCameraSupport by mutableStateOf(false)

    companion object {
        val shared = BotStacksChatPlatform()

        internal var monitoring = Monitoring()
            private set

        /**
         * logout from BotStacks
         */
        fun logout() {
            async {
                try {
                    API.logout()
                } catch (err: Error) {
                    monitoring.error(err)
                }
            }
        }

        /**
         * Register an Firebase Cloud Messaging (FCM) token with our Backend
         */
        fun registerFCMToken(token: String) {
            BotStacksChatStore.current.fcmToken = token
            if (shared.isUserLoggedIn) {
                opbg { API.registerFcmToken(token) }
            }
        }
    }
}

expect class BotStacksChatPlatform(): BotStacksChat {
    val apiKey: String
    val appIdentifier: String
    val scope: CoroutineScope

    /**
     * login to BotStacks Backend
     *
     * @param accessToken BotStacks API key
     * @param userId userId of user to associate session with
     * @param username username for user
     * @param displayName optional display name for user
     * @param picture optional user image (avatar) URL
     */
    suspend fun login(
        userId: String,
        username: String,
        displayName: String? = null,
        picture: String? = null,
    )

    suspend fun load()
}
