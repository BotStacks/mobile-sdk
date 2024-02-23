/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ai.botstacks.sdk.fragment.FUser
import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.utils.contains
import kotlinx.datetime.Instant
import kotlin.math.min

@Stable
data class User(
    override val id: String
) : Identifiable {
    var username by mutableStateOf("")
    var displayName by mutableStateOf<String?>(null)
    var avatar by mutableStateOf<String?>(null)
    var lastSeen by mutableStateOf<Instant?>(null)
    var status by mutableStateOf(OnlineStatus.Offline)
    var statusMessage by mutableStateOf<String?>(null)
    var blocked by mutableStateOf(false)
    var haveContact: Boolean by mutableStateOf(false)
    var description by mutableStateOf<String?>(null)
    var muted by mutableStateOf(false)

    constructor(user: FUser, blocked: Boolean = false, haveContact: Boolean = false, muted: Boolean = false) : this(
        user.id
    ) {
        this.username = user.username
        this.description = user.description
        this.displayName = user.display_name
        this.avatar = user.image
        this.lastSeen = user.last_seen
        this.status = user.status
        this.blocked = blocked
        this.haveContact = haveContact
        this.muted = muted
    }

    init {
        BotStacksChatStore.current.cache.users[id] = this
    }

    fun update(user: FUser) {
        username = user.username
        avatar = user.image
        lastSeen = user.last_seen
        status = user.status
        description = user.description
        muted = user.is_muted ?: false
    }

    val path: String get() = "user/$id"
    val chatPath: String get() = "user/$id/chat"

    @Stable
    val haveChatWith: Boolean get() = BotStacksChatStore.current.dms.contains { it.friend?.id == id }

    val channelsInCommon: List<Chat> get() = BotStacksChatStore.current.groups.filter { it.friend?.id == id }

    @Stable
    val displayNameFb: String
        get() = if (displayName.isNullOrEmpty()) username else displayName ?: username

    @Stable
    val isCurrent: Boolean get() = current?.id == id

    var blocking by mutableStateOf(false)
    var togglingMute by mutableStateOf(false)

//    val sharedMedia by lazy { UserSharedMedia(this) }

    @Stable
    companion object {
        var current by mutableStateOf<User?>(null)

        fun get(id: String): User? {
            return BotStacksChatStore.current.cache.users[id]
        }

        fun get(user: FUser): User {
            val u = get(user.id)
            if (u != null) {
                u.update(user)
                return u
            }
            return User(user)
        }

        fun fetched(id: String) =
            get(id) ?: User(id)

    }
}

fun Collection<User>.usernames(): String {
    val uns = this.toList().slice(0..min(size - 1, 2)).map { it.username }
    if (uns.size < 3) {
        return uns.joinToString(" and ")
    } else if (uns.size == 3) {
        return "${uns[0]}, ${uns[1]} and ${uns[2]}"
    } else {
        return "${uns[0]}, ${uns[1]} and ${size - 3} others"
    }
}