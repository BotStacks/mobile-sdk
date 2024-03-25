//
//  SelectChannelUsersView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/22/24.
//

import Foundation
import SwiftUI
import Combine
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose SelectChannelUsers View.
private struct SelectChannelUsersViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKChannelUserSelectionState
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ViewsKt._SelectChannelUsersView(state: state._state) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// SelectChannelUsersView
///
public struct SelectChannelUsersView: View {
    
    @ObservedObject var state: BSCSDKChannelUserSelectionState
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            SelectChannelUsersViewControllerRepresentable(state: state, measuredWidth: w, measuredHeight: h)
        }
    }
}

public class BSCSDKChannelUserSelectionState : ObservableObject, Codable, Hashable {
  
    private var userIds: [String]
    private var chatId: String?
    
    @Published internal var _state: ChannelUserSelectionState = ChannelUserSelectionState(initialSelections: [])
    
    public init(selections: [User]) {
        print("init state with \(selections)")
        self.userIds = selections.map { $0.id }
        self.chatId = nil
        setup()
    }
    
    public init(chat: Chat) {
        self.chatId = chat.id
        self.userIds = []
        setup()
    }
    
    private func setup() {
        let selections = userIds.compactMap { fetchUser(with: $0) }
        if let chatId = chatId, let chat = fetchChat(with: chatId) {
            _state = ChannelUserSelectionState(chat: chat)
        } else {
            _state = ChannelUserSelectionState(initialSelections: selections)
        }
    }
    
    // Codable conformance
    enum CodingKeys: CodingKey {
        case userIds, chatId
    }
    
    required public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        userIds = try container.decode([String].self, forKey: .userIds)
        chatId = try container.decodeIfPresent(String.self, forKey: .chatId)
    }
    
    var selections: [User] {
        get {
            let s = _state.selections.compactMap { $0 as? User }
            print("\(s.count) items")
            return s
        }
        set {
            _state.selections = NSMutableArray(array: newValue)
            self.objectWillChange.send()
        }
    }
    
    func addUser(user: User) {
        objectWillChange.send()
        _state.addUser(user: user)
    }
    
    func removeUser(user: User) {
        objectWillChange.send()
        _state.removeUser(user: user)
    }
    
    public static func == (lhs: BSCSDKChannelUserSelectionState, rhs: BSCSDKChannelUserSelectionState) -> Bool {
        lhs.userIds == rhs.userIds && lhs.chatId == rhs.chatId
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(userIds)
        hasher.combine(chatId)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(userIds, forKey: .userIds)
        try container.encodeIfPresent(chatId, forKey: .chatId)
    }
    
    private func fetchUser(with id: String) -> User? {
        print("fetchUser \(id)")
        return BotStacksChatStore.companion.current.userWith(id: id)
    }
    
    private func fetchChat(with id: String) -> Chat? {
        print("fetchChat \(id)")
        return BotStacksChatStore.companion.current.chatWith(id: id)
    }
}
