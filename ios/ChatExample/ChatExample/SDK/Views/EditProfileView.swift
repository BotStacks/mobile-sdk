//
//  EditProfileView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/26/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

// The VC representable that abstracts away our KMP Compose EditProfile View.
private struct EditProfileViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKEditProfileState
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    init(state: BSCSDKEditProfileState, measuredWidth: Binding<CGFloat>, measuredHeight: Binding<CGFloat>) {
        self.state = state
        _measuredWidth = measuredWidth
        _measuredHeight = measuredHeight
    }
        
    public func makeViewController(context: Context) -> UIViewController {
        ViewsKt._EditProfileView(state: state._state) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// EditProfileView
///
/// A screen content view for editing the current user.
///
/// - Parameters:
///  - state: The state for the view
///
public struct EditProfileView: View {
    
    @ObservedObject var state: BSCSDKEditProfileState
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            EditProfileViewControllerRepresentable(
                state: state,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

///
/// BSCSDKEditProfileState
///
/// Wrapper observable holder around `EditProfileState` that allows it to implement ``Codable`` and ``Hashable``.
///
///
public class BSCSDKEditProfileState : ObservableObject, Codable, Hashable {

    @Published internal var _state: EditProfileState = EditProfileState()
    
    public init() {
        
    }
    
    required public init(from decoder: Decoder) throws {
      
    }
    
    
    
    public static func == (lhs: BSCSDKEditProfileState, rhs: BSCSDKEditProfileState) -> Bool {
        lhs.hashValue == rhs.hashValue
    }

    public func hash(into hasher: inout Hasher) {
        
    }
    
    public func encode(to encoder: Encoder) throws {

    }

    
    public func update(completion: @escaping (Result<User?, Error>) -> Void) {
        _state.update { chat in
            completion(.success(chat))
        } onError: { error in
            completion(.failure(error.asError()))
        }
    }
}
