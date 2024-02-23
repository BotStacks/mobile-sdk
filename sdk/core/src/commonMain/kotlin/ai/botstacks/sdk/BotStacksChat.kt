/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk

import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.utils.Monitoring
import ai.botstacks.sdk.utils.async
import ai.botstacks.sdk.utils.opbg
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope

abstract class BotStacksChat {
    var loggingIn by mutableStateOf(false)
    var loaded by mutableStateOf(false)
    var isUserLoggedIn by mutableStateOf(false)

    abstract val prefs: Settings

    var onLogout: (() -> Unit)? = null

    var hasGiphySupport by mutableStateOf(false)
    var hasMapsSupport by mutableStateOf(false)
    var hasLocationSupport by mutableStateOf(false)
    var hasCameraSupport by mutableStateOf(false)


    suspend fun login(
        accessToken: String? = null,
        userId: String,
        username: String,
        displayName: String?,
        picture: String?
    ) {
        if (loggingIn) return
        loggingIn = true
        runCatching {
            API.login(
                accessToken = accessToken,
                userId = userId,
                username = username,
                displayName = displayName,
                picture = picture
            )
        }.onSuccess {
            isUserLoggedIn = BotStacksChatStore.current.currentUserID != null
            loggingIn = false
        }.onFailure { err ->
            Monitoring.error(err)
            loggingIn = false
        }
    }

    suspend fun nftLogin(
        wallet: String,
        tokenID: String,
        username: String,
        signature: String,
        picture: String?,
        displayName: String? = null
    ) {
        if (loggingIn) {
            return
        }
        loggingIn = true
        try {
            API.nftLogin(
                wallet = wallet,
                tokenID = tokenID,
                signature = signature,
                picture = picture,
                username = username,
                displayName = displayName
            )
            isUserLoggedIn = BotStacksChatStore.current.currentUserID != null
        } catch (err: Error) {
            Monitoring.error(err)
        }
        loggingIn = false
    }

    companion object {
        val shared = BotStacksChatPlatform()

        suspend fun load() {
            shared.load()
        }

        fun logout() {
            shared.onLogout?.invoke()
            BotStacksChatStore.current.currentUserID = null
            BotStacksChatStore.current.user = null
            User.current = null
            async {
                try {
                    API.logout()
                } catch (err: Error) {
                    Monitoring.error(err)
                }
            }
        }

        fun registerFCMToken(token: String) {
            BotStacksChatStore.current.fcmToken = token
            if (shared.isUserLoggedIn) {
                opbg({
                    API.registerFcmToken(token)
                })
            }
        }
    }
}

expect class BotStacksChatPlatform(): BotStacksChat {
    val apiKey: String
    val appIdentifier: String
    val scope: CoroutineScope

    suspend fun load()
}
