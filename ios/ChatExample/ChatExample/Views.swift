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
                    case .success(let chat):
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

