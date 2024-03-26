//
//  CreateChannelView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/26/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

// The VC representable that abstracts away our KMP Compose SelectChannelUsers View.
private struct CreateChannelViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKCreateChannelState
    
    @State var onSelectUsers: () -> Void
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    init(state: BSCSDKCreateChannelState, onSelectUsers: @escaping () -> Void, measuredWidth: Binding<CGFloat>, measuredHeight: Binding<CGFloat>) {
        self.state = state
        self.onSelectUsers = onSelectUsers
        _measuredWidth = measuredWidth
        _measuredHeight = measuredHeight
    }
        
    public func makeViewController(context: Context) -> UIViewController {
        ViewsKt._CreateChannelView(state: state._state, onSelectUsers: onSelectUsers) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// CreateChannelView
///
/// A screen content view for creating a new Channel.
///
/// - Parameters:
///  - state: The state for the view
///  - onSelectUsers: Callback when the add users icon button is clicked within the User select component. Use this to navigate to a new screen where a user will select participants for this new channel
///
public struct CreateChannelView: View {
    
    @ObservedObject var state: BSCSDKCreateChannelState
    var onSelectUsers: () -> Void
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            CreateChannelViewControllerRepresentable(
                state: state,
                onSelectUsers: onSelectUsers,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

///
/// BSCSDKCreateChannelState
///
/// Wrapper observable holder around [CreateChannelState] that allows it to implement ``Codable`` and ``Hashable``.
///
///
public class BSCSDKCreateChannelState : ObservableObject, Codable, Hashable {

    @Published internal var _state: CreateChannelState = CreateChannelState()
    
    public init() {
        
    }
    
    required public init(from decoder: Decoder) throws {
      
    }
    
    
    
    public static func == (lhs: BSCSDKCreateChannelState, rhs: BSCSDKCreateChannelState) -> Bool {
        lhs.hashValue == rhs.hashValue
    }

    public func hash(into hasher: inout Hasher) {
        
    }
    
    public func encode(to encoder: Encoder) throws {

    }

    
    public func update(completion: @escaping (Result<Chat?, Error>) -> Void) {
        _state.create { chat in
            completion(.success(chat))
        } onError: { error in
            completion(.failure(error.asError()))
        }
    }
}
