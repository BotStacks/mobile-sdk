//
//  ChannelSettingsView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/25/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

// The VC representable that abstracts away our KMP Compose SelectChannelUsers View.
private struct ChannelSettingsViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKChannelSettingsState
    
    @State var onAddUsers: () -> Void
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    init(state: BSCSDKChannelSettingsState, onAddUsers: @escaping () -> Void, measuredWidth: Binding<CGFloat>, measuredHeight: Binding<CGFloat>) {
        self.state = state
        self.onAddUsers = onAddUsers
        _measuredWidth = measuredWidth
        _measuredHeight = measuredHeight
    }
        
    public func makeViewController(context: Context) -> UIViewController {
        ViewsKt._ChannelSettingsView(state: state._state, onAddUsers: onAddUsers) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChannelSettingsView
///
/// A screen content view for displaying settings and details for a given `Chat` channel.
///
/// - Parameters:
///  - state: The state for the view
///  - onAddUsers: Callback when the add users icon button is clicked within the User select component.
///
public struct ChannelSettingsView: View {
    
    @ObservedObject var state: BSCSDKChannelSettingsState
    var onAddUsers: () -> Void
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            ChannelSettingsViewControllerRepresentable(
                state: state,
                onAddUsers: onAddUsers,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

///
/// BSCSDKChannelSettingsState
///
/// Wrapper observable holder around [ChannelSettingsState] that allows it to implement ``Codable`` and ``Hashable``.
///
///
public class BSCSDKChannelSettingsState : ObservableObject, Codable, Hashable {
  
    private var chatId: String
    
    @Published internal var _state: ChannelSettingsState
    
    public init(chat: Chat) {
        self.chatId = chat.id
        _state = ChannelSettingsState(chat: chat)
    }
    
    public init(chatId: String) {
        self.chatId = chatId
        _state = ChannelSettingsState(id: chatId)
    }
    
    // Codable conformance
    enum CodingKeys: CodingKey {
        case chatId
    }
    
    required public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        chatId = try container.decode(String.self, forKey: .chatId)
        _state = ChannelSettingsState(id: chatId)
    }
    
    
    
    public static func == (lhs: BSCSDKChannelSettingsState, rhs: BSCSDKChannelSettingsState) -> Bool {
        lhs.chatId == rhs.chatId
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(chatId)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(chatId, forKey: .chatId)
    }
    
    private func fetchChat(with id: String) -> Chat? {
        return BotStacksChatStore.companion.current.chatWith(id: id)
    }
    
    var participants: [User] {
        get {
            _state.participants.compactMap { $0 as? User }
        }
        set {
            objectWillChange.send()
            _state.participants = NSMutableArray(array: newValue)
        }
    }
    
    public func toggleMute() {
        _state.toggleMute()
    }
    
    public func update(completion: @escaping (Result<Chat?, Error>) -> Void) {
        _state.update { chat in
            completion(.success(chat))
        } onError: { error in
            completion(.failure(error.asError()))
        }
    }
}

