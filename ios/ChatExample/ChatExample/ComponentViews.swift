//
//  ComponentViews.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

internal struct Avatars: View {
    var body: some View {
        ComponentView(title: "Avatar") {
            HStack {
                Avatar(url: "https://randomuser.me/api/portraits/men/81.jpg")
                Avatar(url: "https://randomuser.me/api/portraits/men/80.jpg")
                    .onlineStatus(OnlineStatus.online)
                
                Avatar(url: "https://randomuser.me/api/portraits/men/-1.jpg")
                    .onlineStatus(OnlineStatus.offline)
                    .ifEmpty(Image(systemName: "globe"))
            }
        }
    }
}

internal struct Badges: View {
    var body: some View {
        ComponentView(title: "Badge") {
            HStack {
                Badge(count: 3)
                Badge(label: "Admin")
            }
        }
    }
}

internal struct ChannelRows: View {
    var body: some View {
        ComponentView(title: "ChannelRow") {
            ChannelRow(imageUrls: [], title: "iOS Devs", subtitle: "27 members") {
                print("iOS Devs clicked")
            }
            ChannelRow(
                imageUrls: [
                    "https://randomuser.me/api/portraits/men/81.jpg",
                    "https://randomuser.me/api/portraits/men/84.jpg"
                ],
                title: "Dudes",
                titleColor: Color.green,
                subtitle: "John, Dan"
            ) {
                print("dudes clicked")
            }
        }
    }
}

internal struct ChannelGroups: View {
    
    private let channels: [Chat] = generateChannelList()

    
    var body: some View {
        ComponentView(title: "ChannelGroup") {
            ChannelGroup(channels: channels)
        }
    }
}

internal struct ChatInputExample : View {
        
    private var chat: Chat
    @StateObject private var state: BSCSDKMediaActionSheetState
    
    init(chat: Chat = generateChannel()) {
        self.chat = chat
        self._state = StateObject(wrappedValue: BSCSDKMediaActionSheetState(chat: chat))
    }
    
    var body: some View {
        VStack {
            Spacer()
            ChatInput(chat: chat, onMedia: { state.show() }).padding()
        }.frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .topLeading
        ).sheet(isPresented: state.isShowing) {
            MediaActionSheet(state: state)
        }
    }
}

internal struct ChatListExample: View {

    private var chats: [Chat]
    
    init() {
        chats = generateChatList()
    }
    
    var body: some View {
        ComponentView(title: "ChatList", canScroll: false) {
            ChatList { chat in
                print("\(chat.displayName) clicked")
            }
        }
    }
}

internal struct ChatMessages: View {
    
    private var user1: User
    private var user2: User
    private var user3: User
    private var user4: User
    private var chat: Chat
    
    private var messages: [Message]
    
    init() {
        user1 = generateUser()
        user2 = generateUser()
        user3 = generateUser()
        user4 = generateUser()
        chat = generateChannel(with: [user1, user2, user3, user4], kind: .group)
        messages = [
            generateMessage(from: user1, in: chat),
            generateMessage(from: user1, in: chat),
            generateMessage(from: user2, in: chat),
            generateMessage(from: user3, in: chat, attachments: [generateImageAttachment()]),
            generateMessage(from: user4, in: chat, attachments: [generateLocationAttachment()])
        ]
    }
    
    var body: some View {
        ComponentView(title: "ChatMessage") {
            VStack(alignment: .leading) {
                ForEach(messages, id: \.id) { message in
                    ChatMessage(message: message)
                        .withAvatar(true)
                }
            }.frame(
                maxWidth: .infinity,
                maxHeight: .infinity,
                alignment: .topLeading
            ).padding(EdgeInsets(top: 0, leading: 16, bottom: 16, trailing: 0))
        }
    }
}

internal struct ChatMessagePreviews: View {
    
    private var chats: [Chat]
    
    init() {
        chats = generateChatList()
    }
    
    var body: some View {
        ComponentView(title: "ChatMessagePreview") {
            VStack(alignment: .leading) {
                ForEach(chats, id: \.id) { chat in
                    ChatMessagePreview(chat: chat) {
                        print("chat \(chat.displayName) clicked")
                    }
                }
            }
        }
    }
}

internal struct Headers: View {
    
    @State private var headerState: HeaderState = HeaderState.init(showSearch: true, showSearchClear: true)
    
    var body: some View {
        ComponentView(title: "Header") {
            Spacer(minLength: 50)
            Text("Using title string")
                .font(.callout)
                .frame(alignment: .leading)
            
            Header()
                .title("Settings")
            
            Text("Using title slot")
                .font(.callout)
            
            Header()
                .title {
                    Text("User Details")
                        .font(.headline)
                }
            
            Text("Using icon")
                .font(.callout)
            Header()
                .icon(UIImage.init(systemName: "globe")!)
            
            Text("With menu")
                .font(.callout)
            Header()
                .title("Home")
                .menu {
                    Menu {
                        Button("BotStacks is 🔥", action: { })
                    } label: {
                        Image(systemName: "ellipsis")
                            .tint(Color.black)
                    }
                }
            
            Text("With back navigation")
                .font(.callout)
            Header()
                .backClicked { print("back clicked") }
            
            Text("With endAction")
                .font(.callout)
            Header()
                .title("Settings")
                .backClicked { print("back clicked") }
                .withEndAction(.next(onClick: { print("Next clicked") }))
            
            Text("With multiple actions and searchability")
                .font(.callout)
            Header()
                .withState(headerState)
                .backClicked {
                    if headerState.searchActive {
                        headerState.searchActive = false
                        print("closing search")
                    } else {
                        print("back click")
                    }
                }
                .addClicked { print("add clicked") }
                .composeClicked { print("compose clicked") }
        }
    }
}

internal struct MessageListExample: View {
    
    private var chat: Chat
    
    init() {
        chat = generateChatWithMessages()
    }
    
    var body: some View {
        // uses own internal scroll handling
        ComponentView(title: "MessageList", canScroll: false) {
            MessageList(
                chat: chat,
                onPressUser: { _ in 
                    
                },
                onLongPress: { _ in
                }
        
            )
        }
    }
}

internal struct Spinners: View {
    var body: some View {
        ComponentView(title: "Spinner") {
            Spinner()
        }
    }
}

internal struct UserProfiles: View {
    
    private var user1: User
    private var user2: User
    
    init() {
        self.user1 = User(id: UUID().uuidString)
        self.user1.username = "jon_doe"
        self.user1.displayName = "Jon Doe"
        self.user1.avatar = "https://randomuser.me/api/portraits/men/81.jpg"
        
        self.user2 = User(id: UUID().uuidString)
        self.user2.username = "jane_doe"
        self.user2.displayName = "Jane Doe"
        self.user2.avatar = "https://randomuser.me/api/portraits/women/21.jpg"
    }
    
    var body: some View {
        ComponentView(title: "UserProfile") {
            VStack {
                HStack {
                    UserProfile(user: user1)
                    UserProfile(user: user2)
                }
            }
        }
    }
}

internal struct UserSelectExample: View {
    
    @EnvironmentObject private var router: Router
    
    @StateObject private var state: BSCSDKChannelUserSelectionState = BSCSDKChannelUserSelectionState(selections: [])
    
    var body: some View {
        ComponentView(title: "UserSelect") {
            UserSelect(
                selectedUsers: $state.selections,
                canRemove: true,
                showAdd: true,
                onRemove: { user in
                    state.removeUser(user: user)
                },
                onAddSelected: { router.navigate(to: .channeluserselect(state)) }
            ).padding(.vertical)
        }
    }
}

internal struct ComponentView<Content: View>: View {
    
    @EnvironmentObject var router: Router
    
    private var title: String
    private var withScrollView: Bool
    private var onBackClicked: (() -> Void)? = nil
    private var endAction: EndAction? = nil
    private var content: () -> Content
    
    init(
        title: String,
        canScroll: Bool = true,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.title = title
        self.withScrollView = canScroll
        self.content = content
    }
    
    private func applyEndActionToHeader(to view: Header) -> Header {
            if let endAction = endAction {
                return view.withEndAction(endAction)
            } else {
                return view
            }
        }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            applyEndActionToHeader(
                to: Header()
                    .title(title)
                    .backClicked {
                        if let backClick = onBackClicked {
                            backClick()
                        } else {
                            router.navigateBack()
                        }
                    }
            )
                
            
            if withScrollView {
                ScrollView(content: content)
            } else {
                content()
            }
        }
        .ignoresSafeArea()
        .navigationBarTitle(Text(""), displayMode: .inline) // Hide navigation bar title
        .navigationBarBackButtonHidden()
    }
}

extension ComponentView {
    func onBack(_ block: @escaping () -> Void) -> ComponentView {
        var snapshot = self
        snapshot.onBackClicked = block
        return snapshot
    }
    
    func withEndAction(_ action: EndAction) -> ComponentView {
        var snapshot = self
        snapshot.endAction = action
        return snapshot
    }
}
