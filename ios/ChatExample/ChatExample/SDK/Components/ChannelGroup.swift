//
//  ChannelGroup.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChannelGroup component.
private struct ChannelGroupViewControllerRepresentable : VCRepresentable {
    
    @State var channels: [Chat]
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChannelGroup(channels: channels) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChannelGroup
///
/// Renders a given list of ``Chat`` channels in a `Column`.
///
/// This renders each chat in its own ``ChatExample/ChannelRow``, with the `Chat#displayName` as the title and the subtitle showing a preview of the members with in it.
///
///  This is utilized in the ``ChatExample/UserDetailsView`` to show channels the current user has in common with any other user.
///
///  - Parameters:
///   - channels: List of channels to show in the list.
///
public struct ChannelGroup: View {
    private var channels: [Chat]
    
    public init(channels: [Chat]) {
        self.channels = channels
    }
    
    public var body: some View {
        MeasuredView { w, h in
            ChannelGroupViewControllerRepresentable(channels: channels, measuredWidth: w, measuredHeight: h)
        }
    }
}
