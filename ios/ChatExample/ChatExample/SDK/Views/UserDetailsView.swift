//
//  UserDetailsView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/26/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

// The VC representable that abstracts away our KMP Compose UserDetails View.
private struct UserDetailsViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKUserDetailsState
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    init(state: BSCSDKUserDetailsState, measuredWidth: Binding<CGFloat>, measuredHeight: Binding<CGFloat>) {
        self.state = state
        _measuredWidth = measuredWidth
        _measuredHeight = measuredHeight
    }
        
    public func makeViewController(context: Context) -> UIViewController {
        ViewsKt._UserDetailsView(state: state._state) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// UserDetailsView
///
/// A screen content view for displaying details about a given `User`.
///
/// - Parameters:
///  - state: The state for the view
///
public struct UserDetailsView: View {
    
    @ObservedObject var state: BSCSDKUserDetailsState
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            UserDetailsViewControllerRepresentable(
                state: state,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

///
/// BSCSDKUserDetailsState
///
/// Wrapper observable holder around `UserDetailsState` that allows it to implement ``Codable`` and ``Hashable``.
///
///
public class BSCSDKUserDetailsState : ObservableObject, Codable, Hashable {
  
    private var userId: String
    
    @Published internal var _state: UserDetailsState
    
    public init(user: User) {
        self.userId = user.id
        _state = UserDetailsState(user: user)
    }
    
    public init(userId: String) {
        self.userId = userId
        _state = UserDetailsState(id: userId)
    }
    
    // Codable conformance
    enum CodingKeys: CodingKey {
        case userId
    }
    
    required public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        userId = try container.decode(String.self, forKey: .userId)
        _state = UserDetailsState(id: userId)
    }
    
    
    
    public static func == (lhs: BSCSDKUserDetailsState, rhs: BSCSDKUserDetailsState) -> Bool {
        lhs.userId == rhs.userId
    }

    public func hash(into hasher: inout Hasher) {
        hasher.combine(userId)
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(userId, forKey: .userId)
    }
    
    private func fetchUser(with id: String) -> User? {
        return BotStacksChatStore.companion.current.userWith(id: id)
    }
}


