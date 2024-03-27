//
//  UserProfile.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

/// The VC representable that abstracts away our KMP Compose UserProfile component.
private struct UserProfileViewControllerRepresentable : VCRepresentable {
    
    @State var user: User
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._UserProfile(user: user) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// UserProfile
///
/// Renders an ``Avatar`` and the display name for a given ``User`` in a centered Column.
///
/// - Parameters:
///   - user: The user to show in the view
public struct UserProfile: View {
    
    private var user: User
    
    init(user: User) {
        self.user = user
    }
    
    public var body: some View {
        MeasuredView(content: { w, h in
            UserProfileViewControllerRepresentable(user:user, measuredWidth: w, measuredHeight: h)
        })
    }
}

