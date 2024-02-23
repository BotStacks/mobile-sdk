/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.*
import ai.botstacks.sdk.API
import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.utils.bg
import ai.botstacks.sdk.utils.launch

@Stable
class Settings {

    var notifications by mutableStateOf(NotificationSetting.all)
    var availabilityStatus by mutableStateOf(OnlineStatus.Online)
    var blocked = mutableStateListOf<String>()
    var muted = mutableStateListOf<String>()
    val lastUsedReactions = mutableStateListOf<String>()

    fun init() {
        notifications = BotStacksChat.shared.prefs.getStringOrNull("notifications")
            ?.let { NotificationSetting.valueOf(it) }
            ?: NotificationSetting.all
        availabilityStatus = BotStacksChat.shared.prefs.getStringOrNull("availability")
            ?.let { OnlineStatus.valueOf(it) }
            ?: OnlineStatus.Online
        blocked =
            BotStacksChat.shared.prefs.getStringOrNull("blocked")
                ?.split(", ")?.toSet()?.toMutableStateList() ?: mutableStateListOf()
        muted =
            BotStacksChat.shared.prefs.getStringOrNull("muted")
                ?.split(", ")?.toSet()?.toMutableStateList() ?: mutableStateListOf()
        lastUsedReactions.clear()
        lastUsedReactions.addAll(
            BotStacksChat.shared.prefs.getString("reactions", "😀,🤟,❤️,🔥,🤣").split(",")
        )
    }


    fun setNotifications(setting: NotificationSetting, isSync: Boolean = false) {
        this.notifications = setting
        BotStacksChat.shared.prefs.putString("notifications", setting.rawValue)
        if (isSync) return
        launch { bg { API.updateNotifications(setting) } }
    }

    fun setAvailability(setting: OnlineStatus, isSync: Boolean = false) {
        this.availabilityStatus = setting
        BotStacksChat.shared.prefs.putString("availability", setting.rawValue)
        if (isSync) return
        launch { bg { API.updateAvailability(setting) } }
    }

    fun setBlock(uid: String, blocked: Boolean) {
        if (blocked) {
            if (!this.blocked.contains(uid)) {
                this.blocked.add(uid)
                BotStacksChat.shared.prefs.putString("blocked", this.blocked.toSet().joinToString())
            }
        } else {
            this.blocked.remove(uid)
            BotStacksChat.shared.prefs.putString("blocked", this.blocked.toSet().toString())
        }
    }

    fun setMuted(uid: String, muted: Boolean) {
        if (muted) {
            if (!this.muted.contains(uid)) {
                this.muted.add(uid)
                BotStacksChat.shared.prefs.putString("muted", this.muted.toSet().joinToString())
            }
        } else {
            this.muted.remove(uid)
            BotStacksChat.shared.prefs.putString("muted", this.muted.toSet().joinToString())
        }
    }

    fun onReaction(emoji: String) {
        if (lastUsedReactions.contains(emoji)) {
            lastUsedReactions.remove(emoji)
        }
        lastUsedReactions.add(0, emoji)
        if (lastUsedReactions.size > 5) {
            lastUsedReactions.removeRange(5, lastUsedReactions.size - 1)
        }
        BotStacksChat.shared.prefs.putString("reactions", lastUsedReactions.joinToString())
    }
}