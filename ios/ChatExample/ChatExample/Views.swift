//
//  Views.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/23/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

internal struct ChannelUserSelect : View {
    
    @ObservedObject var state: BSCSDKChannelUserSelectionState
    
    var body: some View {
        ComponentView(
            title: "Select Users",
            canScroll: false
        ) {
            SelectChannelUsersView(state: state)
                .frame(
                    maxWidth: .infinity,
                    maxHeight: .infinity,
                    alignment: .topLeading
                )
        }
    }
}

internal struct ChannelSettingsExample: View {
    
    @EnvironmentObject var router: Router
    
    var body: some View {
        ChatList { chat in
            router.navigate(to: .channelsettings(chat.id))
        }
    }
}

internal struct ChannelSettingsExample_SettingsView: View {
    
    @EnvironmentObject var router: Router
    
    private var chatId: String
    
    @StateObject private var state: BSCSDKChannelSettingsState
    
    @State private var showError = false
    @State private var errorMessage = ""
    @State private var saving = false
        
    init(chatId: String) {
        self.chatId = chatId
        self._state = StateObject(wrappedValue: BSCSDKChannelSettingsState(chatId: chatId))
    }
    
    var body: some View {
        ZStack {
            ComponentView(title: "Channel Details", canScroll: false) {
                VStack {
                    ChannelSettingsView(state: state) {
                        router.navigate(to: .channelsettings_userselect(state))
                    }
                }
            }.withEndAction(EndAction.save(onClick: {
                saving = true
                state.update { result in
                    saving = false
                    switch result {
                    case .success( _):
                        router.navigateBack()
                    case .failure(let error):
                        self.errorMessage = "An error occurred.\n\(error.localizedDescription)"
                        self.showError = true
                    }
                }
            })).alert(isPresented: $showError) {
                Alert(title: Text("Error"), message: Text(errorMessage), dismissButton: .default(Text("OK")))
            }
            
            if saving {
                ProgressOverlay()
            }
        }
    }
}

internal struct ChannelSettings_UserSelect : View {
    
    @EnvironmentObject var router: Router
    @ObservedObject var channelState: BSCSDKChannelSettingsState
    private var state: BSCSDKChannelUserSelectionState

    init(state: BSCSDKChannelSettingsState) {
        self.channelState = state
        self.state = BSCSDKChannelUserSelectionState(selections: state.participants)
    }
        
    var body: some View {
        ComponentView(
            title: "Select Users",
            canScroll: false
        ) {
            SelectChannelUsersView(state: state)
                .frame(
                    maxWidth: .infinity,
                    maxHeight: .infinity,
                    alignment: .topLeading
                )
        }.onBack {
            channelState.participants = state.selections
            router.navigateBack()
        }
    }
}

internal struct EditProfile: View {
    
    @EnvironmentObject var router: Router
    
    @State private var state: BSCSDKEditProfileState = BSCSDKEditProfileState()
    
    @State private var showError = false
    @State private var errorMessage = ""
    @State private var saving = false
    
    var body: some View {
        ZStack {
            ComponentView(
                title: "Edit Profile",
                canScroll: false
            ) {
                EditProfileView(state: state)
            }.withEndAction(EndAction.save(onClick: {
                saving = true
                state.update { result in
                    saving = false
                    switch result {
                    case .success( _):
                        router.navigateBack()
                    case .failure(let error):
                        self.errorMessage = "An error occurred.\n\(error.localizedDescription)"
                        self.showError = true
                    }
                }
            })).alert(isPresented: $showError) {
                Alert(title: Text("Error"), message: Text(errorMessage), dismissButton: .default(Text("OK")))
            }
            
            if saving {
                ProgressOverlay()
            }
        }
    }
}

internal struct UserDetailsExample_UserSelect: View {
    
    @EnvironmentObject var router: Router
    
    
    private var users: [User]
    
    init() {
        self.users = generateUserList()
    }
    
    var body: some View {
        List {
            ForEach(users, id: \.id) { user in
                
            }
        }
        VStack(alignment: .leading) {
            ForEach(users, id: \.id) { user in
                ListRow(title: user.displayNameFb) {
                    router.navigate(to: .userdetails(user.id))
                }
            }
        }.frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .topLeading
        ).padding(EdgeInsets(top: 0, leading: 16, bottom: 16, trailing: 0))
    }
    
    private struct ListRow: View {
        
        public var title: String
        public var onClick: () -> Void
        
        var body: some View {
            HStack {
                Text(title)
                Spacer()
            }.contentShape(Rectangle())
             .onTapGesture {
                 onClick()
             }
        }
    }
}

internal struct UserDetailsExample_Details: View {
    
    private var userId: String
    
    @State private var state: BSCSDKUserDetailsState
        
    init(userId: String) {
        self.userId = userId
        self._state = State(wrappedValue: BSCSDKUserDetailsState(userId: userId))
    }
    
    var body: some View {
        ComponentView(
            title: "Details",
            canScroll: false
        ) {
            UserDetailsView(state: state)
        }
    }
}
