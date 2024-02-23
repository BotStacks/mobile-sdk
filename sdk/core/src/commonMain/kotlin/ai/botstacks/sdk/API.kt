/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk

import ai.botstacks.sdk.fragment.FUser
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Member
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.state.onCoreEvent
import ai.botstacks.sdk.state.onMeEvent
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.type.ChatRegisterInput
import ai.botstacks.sdk.type.CreateGroupInput
import ai.botstacks.sdk.type.DeviceType
import ai.botstacks.sdk.type.EthLoginInput
import ai.botstacks.sdk.type.LoginInput
import ai.botstacks.sdk.type.MemberRole
import ai.botstacks.sdk.type.ModMemberInput
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.type.OnlineStatus
import ai.botstacks.sdk.type.SendMessageInput
import ai.botstacks.sdk.type.UpdateGroupInput
import ai.botstacks.sdk.type.UpdateMessageInput
import ai.botstacks.sdk.type.UpdateProfileInput
import ai.botstacks.sdk.utils.Monitoring
import ai.botstacks.sdk.utils.ift
import ai.botstacks.sdk.utils.uuid
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


object Server {
    val host = SdkConfig.HOST
    val ssl = SdkConfig.SSL
    val http = "http${if (ssl) "s" else ""}://${host}"
    val ws = "ws${if (ssl) "s" else ""}://${host}"
}

object API {
    lateinit var deviceId: String

    var authToken: String? by Delegates.observable(null) { property, oldValue, newValue ->
        if (newValue != null) {
            BotStacksChat.shared.prefs.putString("auth-token", newValue)
        }
        client.subscriptionNetworkTransport
    }

    var client = ApolloClient.Builder()
        .serverUrl(Server.http + "/graphql")
        .subscriptionNetworkTransport(
            WebSocketNetworkTransport.Builder()
                .protocol(GraphQLWsProtocol.Factory(
                    connectionPayload = {
                        authToken?.let {
                            mapOf(
                                "authToken" to it,
                                "apiKey" to BotStacksChat.shared.apiKey
                            )
                        } ?: mapOf("apiKey" to BotStacksChat.shared.apiKey)
                    }
                ))
                .serverUrl(Server.ws + "/graphql")
                .build()
        )
        .addHttpInterceptor(object : HttpInterceptor {
            override suspend fun intercept(
                request: HttpRequest,
                chain: HttpInterceptorChain
            ): HttpResponse {
                return chain.proceed(
                    request.newBuilder().apply {
                        authToken?.let { addHeader("Authorization", "Bearer $it") }
                        addHeader("X-API-Key", BotStacksChat.shared.apiKey)
                        addHeader("X-Device-ID", API.deviceId)
                        addHeader("Referer",  BotStacksChat.shared.appIdentifier)
                    }.build()
                )
            }
        })
        .addHttpInterceptor(LoggingInterceptor())
        .addInterceptor(object : ApolloInterceptor {
            override fun <D : Operation.Data> intercept(
                request: ApolloRequest<D>,
                chain: ApolloInterceptorChain
            ): Flow<ApolloResponse<D>> {
                return chain.proceed(request).onEach { response ->
                    response.errors?.let {
                        for (err in it) {
                            println("Got error " + err.message)
                            if (err.message == "login required") {
                                withContext(Dispatchers.Main) {
                                    onLogout()
                                }
                            }
                        }
                    }
                }
            }

        })
        .build()


    fun init() {
        var deviceId = BotStacksChat.shared.prefs.getStringOrNull("device-id")
        if (deviceId == null) {
            deviceId = uuid()
            BotStacksChat.shared.prefs.putString("device-id", deviceId)
        }
        this.deviceId = deviceId
        this.authToken = BotStacksChat.shared.prefs.getStringOrNull("auth-token")
        if (authToken != null) {
            subscribe()
        }
    }


    suspend fun getMessages(chat: String, skip: Int = 0, limit: Int = 40) =
        client.query(
            ListMessagesQuery(
                chat = chat,
                offset = Optional.present(skip),
                count = Optional.present(limit)
            )
        ).execute().dataOrThrow().messages.map { Message.get(it.fMessage) }


    suspend fun send(
        chat: String,
        id: String,
        inReplyTo: String?,
        text: String? = null,
        attachments: List<AttachmentInput>? = null,
    ) =
        client.mutation(
            SendMessageMutation(
                SendMessageInput(
                    chat = chat,
                    id = Optional.present(id),
                    parent = Optional.presentIfNotNull(inReplyTo),
                    text = Optional.presentIfNotNull(text),
                    attachments = Optional.presentIfNotNull(attachments),
                )
            )
        ).execute().data?.sendMessage?.fMessage?.let { Message.get(it) }

    suspend fun dm(user: String) =
        client.mutation(DMMutation(user)).execute().dataOrThrow().dm?.let {
            val chat = Chat.get(it.fChat)
            chat.membership?.let { membership ->
                BotStacksChatStore.current.memberships.add(membership)
            }
            chat
        }

    suspend fun updateChat(
        id: String,
        name: String,
        private: Boolean,
        description: String? = null,
        image: String? = null,
    ) = client.mutation(
        UpdateGroupMutation(
            UpdateGroupInput(
                id = id,
                name = Optional.present(name),
                _private = Optional.present(private),
                description = Optional.presentIfNotNull(description),
                image = Optional.presentIfNotNull(image),
            )
        )
    ).execute().dataOrThrow().updateGroup.let {
         if (!it) throw IllegalStateException("Result from update was false")
        getChat(g = id)?.also { chat ->
            chat.membership?.let { membership ->
                BotStacksChatStore.current.memberships.toMutableList().apply {
                    val index = indexOf(membership)
                    if (index >= 0) {
                        set(index, membership)
                    }
                }
            }
        }
    }

    suspend fun createChat(
        name: String,
        _private: Boolean,
        description: String? = null,
        image: String? = null,
        invites: List<String> = listOf()
    ) = client.mutation(
        CreateGroupMutation(
            CreateGroupInput(
                name = name,
                _private = Optional.presentIfNotNull(_private),
                description = Optional.presentIfNotNull(description),
                image = Optional.presentIfNotNull(image),
                invites = Optional.presentIfNotNull(invites)
            )
        )
    ).execute().dataOrThrow().createGroup?.fChat?.let {
        val chat = Chat.get(it)
        chat.membership?.let { membership ->
            BotStacksChatStore.current.memberships.add(membership)
        }
        chat
    }

    suspend fun deleteChat(id: String) =
        client.mutation(DeleteGroupMutation(id)).execute().data?.deleteGroup

    suspend fun joinChat(g: String) =
        client.mutation(JoinChatMutation(g)).execute().data?.join?.let { Member.get(it.fMember) }

    suspend fun leaveChat(g: String) = client.mutation(LeaveChatMutation(g)).execute().data?.leave
    suspend fun getChat(g: String) =
        client.query(GetChatQuery(g)).execute().data?.chat?.fChat?.let { Chat.get(it) }

    suspend fun modAdmin(g: String, uid: String, promote: Boolean) = client.mutation(
        ModMemberRoleMutation(
            ModMemberInput(
                chat = g,
                user = uid,
                role = Optional.present(ift(promote, MemberRole.Admin, MemberRole.Member))
            )
        )
    ).execute().data?.modMember

    suspend fun dismissAdmin(g: String, uid: String) = modAdmin(g, uid, true)
    suspend fun promoteAdmin(g: String, uid: String) = modAdmin(g, uid, true)

    suspend fun deleteMessage(id: String) =
        client.mutation(DeleteMessageMutation(id)).execute().data?.removeMessage

    suspend fun favorite(message: String, remove: Boolean) =
        if (remove) client.mutation(UnfavoriteMutation(message)).execute().data?.unfavorite else
            client.mutation(FavoriteMutation(message)).execute().data?.favorite

    suspend fun favorites(skip: Int, limit: Int) =
        client.query(
            ListFavoritesQuery(
                count = Optional.present(limit),
                offset = Optional.present(skip)
            )
        ).execute().dataOrThrow().favorites.map { Message.get(it.fMessage) }

    suspend fun react(mid: String, emoji: String) =
        client.mutation(ReactMutation(mid, Optional.present(emoji))).execute().data?.react

    suspend fun invites() = client.query(GetInvitesQuery()).execute().dataOrThrow().invites

    suspend fun getUsers(skip: Int, limit: Int) = client.query(
        ListUsersQuery(
            count = Optional.present(limit),
            offset = Optional.present(skip)
        )
    )
        .execute().dataOrThrow().users.map { User.get(it.fUser) }

    suspend fun editMessageText(id: String, text: String) =
        client.mutation(
            UpdateMessageMutation(
                UpdateMessageInput(
                    id = id,
                    text = Optional.present(text)
                )
            )
        )

    suspend fun dismissInvites(g: String) =
        client.mutation(DismissInvitesMutation(g)).execute().data?.dismissInvites

    suspend fun acceptInvites(g: String) = joinChat(g)

    suspend fun invite(users: List<String>, toChat: String) =
        client.mutation(InviteUsersMutation(toChat, users))
            .execute().data?.inviteMany?.map { Member.get(it.fMember) }

//    suspend fun getSharedMedia(uid: String) =
//        _default.getUserMessages(uid, 0, 10, MessageType.image).result().map { it.m() }

    suspend fun onLogin(token: String, user: FUser) {
        onToken(
            token
        )
        onUser(User.get(user))
    }

    suspend fun onUser(user: User) {
        BotStacksChatStore.current.user = user
        BotStacksChatStore.current.currentUserID = user.id
        User.current = user
        try {
            BotStacksChatStore.current.loadAsync()
            subscribe()
        } catch (err: Error) {
            Monitoring.error(err)
        }
    }

    private val subscriptionScope = CoroutineScope(Dispatchers.IO)

    fun subscribe() {
        subscriptionScope.launch {
            client.subscription(CoreSubscription()).toFlow().collectLatest {
                it.data?.let {
                    println("Got subscription event $it")
                    BotStacksChat.shared.scope.launch {
                        BotStacksChatStore.current.onCoreEvent(it.core)
                    }
                } ?: it.errors?.forEach {
                    Monitoring.error(it.message)
                }
            }

            client.subscription(MeSubscription()).toFlow().collectLatest {
                it.data?.let {

                    BotStacksChat.shared.scope.launch {
                        BotStacksChatStore.current.onMeEvent(it.me)
                    }
                } ?: it.errors?.forEach {
                    Monitoring.error(it.message)
                }
            }
        }
    }

    fun unsubscribe() {
        subscriptionScope.cancel()
    }

    fun onToken(access: String) {
        authToken = access
    }

    suspend fun login(
        accessToken: String?,
        userId: String,
        username: String,
        displayName: String?,
        picture: String?
    ) {
        val res = client.mutation(
            LoginMutation(
                LoginInput(
                    user_id = userId,
                    access_token = Optional.presentIfNotNull(accessToken),
                    image = Optional.presentIfNotNull(picture),
                    username = username,
                    display_name = Optional.presentIfNotNull(displayName)
                )
            )
        ).execute().dataOrThrow().login
        if (res != null) {
            onLogin(res.token, res.user.fUser)
            return
        }
        throw Error("There was a problem logging in")
    }

    suspend fun login(
        email: String,
        password: String,
    ) {
        val res = client.mutation(
            BasicLoginMutation(
                email = email,
                password = password,
            )
        ).execute().dataOrThrow().basicLogin
        if (res != null) {
            onLogin(res.token, res.user.fUser)
            return
        }
        throw Error("There was a problem logging in")
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        picture: String?
    ) {
        val res = client.mutation(
            ChatRegisterMutation(
                ChatRegisterInput(
                    email = email,
                    password = password,
                    username = displayName,
                    image = Optional.presentIfNotNull(picture)
                )
            )
        ).execute().dataOrThrow().chatRegister
        if (res != null) {
            onLogin(res.token, res.user.fUser)
            return
        }
        throw Error("There was a problem logging in")
    }

    suspend fun nftLogin(
        wallet: String,
        tokenID: String,
        signature: String,
        picture: String?,
        username: String,
        displayName: String?
    ) {
        val res = client.mutation(
            EthLoginMutation(
                EthLoginInput(
                    wallet = wallet,
                    signed_message = signature,
                    token_id = tokenID,
                    username = username,
                    image = Optional.presentIfNotNull(picture)
                )
            )
        ).execute().dataOrThrow().ethLogin
        if (res != null) {
            onLogin(res.token, res.user.fUser)
        } else {
            throw Error("There was a problem logging in")
        }
    }

    suspend fun getUser(id: String): User? =
        client.query(GetUserQuery(id)).execute().data?.user?.fUser?.let { User.get(it) }

    suspend fun logout() {
        client.mutation(LogoutMutation()).execute().data?.logout
        BotStacksChat.shared.scope.launch {
            onLogout()
        }
    }

    fun onLogout() {
        BotStacksChat.shared.onLogout?.invoke()
        unsubscribe()
        authToken = null
        val store = BotStacksChatStore.current
        store.user = null
        store.currentUserID = null
        BotStacksChatStore.current = BotStacksChatStore()
        BotStacksChat.shared.isUserLoggedIn = false
        BotStacksChat.shared.loaded = true
    }


    suspend fun block(id: String, isBlock: Boolean) =
        if (isBlock)
            client.mutation(BlockMutation(id)).execute().data?.block
        else
            client.mutation(UnblockMutation(id)).execute().data?.unblock

    suspend fun mute(id: String, isMute: Boolean) =
        if (isMute) client.mutation(MuteMutation(id)).execute().data?.mute
        else client.mutation(UnmuteMutation(id)).execute().data?.unmute

    suspend fun updateProfile(input: UpdateProfileInput) = client.mutation(
        UpdateProfileMutation(
            input
        )
    ).execute().data?.updateProfile

    suspend fun updateNotifications(setting: NotificationSetting) = updateProfile(
        UpdateProfileInput(
            notification_setting = Optional.present(
                setting
            )
        )
    )


    suspend fun updateAvailability(status: OnlineStatus) = updateProfile(
        UpdateProfileInput(
            status = Optional.present(
                status
            )
        )
    )

    suspend fun updateChatNotifications(id: String, setting: NotificationSetting) =
        client.mutation(SetNotificationSettingMutation(id, setting)).execute()
            .data?.setNotificationSetting

    suspend fun getContacts(existing: List<String>) =
        client.mutation(SyncContactsMutation(existing)).execute().data?.syncContacts

    suspend fun chats(skip: Int, limit: Int) =
        client.query(
            ListGroupsQuery(
                count = Optional.present(limit),
                offset = Optional.present(skip)
            )
        )
            .execute().dataOrThrow().groups.map { Chat.get(it.fChat) }

    suspend fun getMessage(id: String) = client.query(GetMessageQuery(id)).execute()
        .dataOrThrow().message?.let { Message.get(it.fMessage) }

    suspend fun getReplies(mid: String, skip: Int, limit: Int) =
        client.query(
            ListRepliesQuery(
                mid,
                skip = Optional.present(skip),
                limit = Optional.present(limit)
            )
        )
            .execute().dataOrThrow().replies.map { Message.get(it.fMessage) }

    suspend fun registerFcmToken(token: String) =
        client.mutation(RegisterPushMutation(token, DeviceType.ANDROID, Optional.present(true)))
            .execute()
            .dataOrThrow().registerPush

    suspend fun me(): User =
        client.query(GetMeQuery()).execute().dataOrThrow().let { data ->
            data.memberships.forEach {
                Chat.get(it.chat.fChat)
            }
            BotStacksChatStore.current.memberships.addAll(data.memberships.map { Member.get(it.fMember) })
            data.me.let {
                BotStacksChatStore.current.settings.blocked.addAll(it.blocks ?: listOf())
                User.get(it.fUser)
            }
        }

    suspend fun markChatRead(id: String) =
        client.mutation(MarkChatReadMutation(id)).execute().dataOrThrow().markChatRead

}