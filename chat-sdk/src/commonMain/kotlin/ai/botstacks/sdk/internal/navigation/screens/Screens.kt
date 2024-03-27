package ai.botstacks.sdk.internal.navigation.screens

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.internal.navigation.LocalPlatformNavigator
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.internal.navigation.ui.channels.ChannelDetailsScreen
import ai.botstacks.sdk.internal.navigation.ui.channels.CreateChannelScreen
import ai.botstacks.sdk.internal.navigation.ui.channels.SelectChannelUsersScreen
import ai.botstacks.sdk.internal.navigation.ui.chats.ChatsListScreen
import ai.botstacks.sdk.internal.navigation.ui.chats.ConversationRouter
import ai.botstacks.sdk.internal.navigation.ui.chats.FavoritesMessagesScreen
import ai.botstacks.sdk.internal.navigation.ui.profile.ProfileView
import ai.botstacks.sdk.ui.views.ChannelSettingsState
import ai.botstacks.sdk.ui.views.ChannelUserSelectionState
import ai.botstacks.sdk.ui.views.CreateChannelState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey

internal data object ChatListScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current
        ChatsListScreen(
            openChat = { navigator.push(ChatScreen(it.id)) },
            onCreateChannel = { navigator.push(CreateChannelScreen) },
            editProfile = { navigator.push(EditProfileScreen) },
            openFavorites = { navigator.push(FavoriteMessagesScreen) },
            onConfirmedLogout = { BotStacksChat.logout() }
        )
    }
}

internal data class ChatScreen(val chatId: String) : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current
        ConversationRouter(
            gid = chatId,
            back = { navigator.pop() },
            openEditChat = {
                if (it.isGroup) {
                    navigator.push(ChannelDetailsScreen(it))
                } else {
                    // TODO DM
                }
            },
            openInvite = {},
            openProfile = { navigator.push(UserDetailsScreen(it)) },
            openReply = {}
        )
    }
}

internal data object CreateChannelScreen : Screen {
    override val key = uniqueScreenKey

    val state = CreateChannelState()

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current

        CreateChannelScreen(
            state = state,
            onBackClicked = { navigator.pop() },
            onChannelCreated = {
                navigator.replaceAll(listOf(ChatListScreen, ChatScreen(it)))
            },
            onSelectUsers = {
                navigator.push(
                    SelectUsersScreen(
                        onSelections = {
                            state.participants = it.toMutableStateList()
                        }
                    )
                )
            }
        )
    }
}

internal data class ChannelDetailsScreen(val chat: Chat) : Screen {

    override val key = uniqueScreenKey

    val state = ChannelSettingsState(chat)

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current

        ChannelDetailsScreen(
            state = state,
            onBackClicked = { navigator.pop() },
            onOpenAnnouncements = { /* TODO */ },
            onAddUsers = {
                navigator.push(
                    SelectUsersScreen(
                        chat = chat,
                        onSelections = {
                            state.participants.clear()
                            state.participants.addAll(it)
                        }
                    )
                )
            }
        )
    }
}

private data class SelectUsersScreen(
    val chat: Chat? = null,
    val users: List<User> = emptyList(),
    val onSelections: (List<User>) -> Unit = { },
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current

        val state = remember {
            if (chat != null) {
                ChannelUserSelectionState(chat = chat,)
            } else {
                ChannelUserSelectionState(initialSelections = users.toMutableStateList())
            }
        }

        SelectChannelUsersScreen(
            state = state,
            onBackClicked = {
                onSelections(state.selections)
                navigator.pop()
            }
        )
    }
}

internal data object EditProfileScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current
        ProfileView(user = User.current!!, onBackClicked = { navigator.pop() })
    }
}

internal data class UserDetailsScreen(val user: User) : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current
        ProfileView(user = user, onBackClicked = { navigator.pop() })
    }
}

internal data object FavoriteMessagesScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current

        FavoritesMessagesScreen(
            back = { navigator.pop() },
            openReplies = { },
            openProfile = { navigator.push(UserDetailsScreen(it)) },
            scrollToTop = 0
        )
    }
}


