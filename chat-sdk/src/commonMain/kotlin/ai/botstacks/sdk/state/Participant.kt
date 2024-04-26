package ai.botstacks.sdk.state

import androidx.compose.runtime.Stable
import ai.botstacks.sdk.fragment.FMember
import ai.botstacks.sdk.internal.state.BotStacksChatStore
import ai.botstacks.sdk.internal.state.toRole
import kotlinx.datetime.Instant

/**
 * A representation for a participant in a given [Chat].
 */
@Stable
data class Participant(
    internal val user_id: String,
    internal val chat_id: String,
    internal val created_at: Instant,
    internal var role: MemberRole
) {
    /**
     * Whether this participant is currently an active member of the corresponding [Chat]
     */
    val isMember: Boolean
        get() = when (role) {
            MemberRole.Admin, MemberRole.Member, MemberRole.Owner -> true
            else -> false
        }

    /**
     * Whether this participant is currently an admin of the corresponding [Chat]
     */
    val isAdmin: Boolean
        get() = when (role) {
            MemberRole.Admin, MemberRole.Owner -> true
            else -> false
        }

    /**
     * The [User] representation for the [Participant]
     */
    val user: User
        get() = User.get(user_id)!!

    /**
     * The [Chat] for this [Participant]
     */
    val chat: Chat
        get() = Chat.get(chat_id)!!

    init {
        val memberships = BotStacksChatStore.current.memberships
        if (!memberships.contains(this)) {
            memberships.add(this)
        }
    }

    companion object {
        internal fun get(props: FMember): Participant {
            User.get(props.user.fUser)
            return Participant(props.user.fUser.id, props.chat_id, props.created_at, props.role.toRole())
        }
    }
}