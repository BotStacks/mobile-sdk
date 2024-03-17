//
//  Badge.swift
//  BotStacksChatSDK
//
//  Created by Brandon McAnsh on 3/10/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

/// The VC representable that abstracts away our KMP Compose Badge component for counts.
private struct BadgeCountViewControllerRepresentable : VCRepresentable {
    
    @State public var count: Int32
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._Badge(count: count) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

/// The VC representable that abstracts away our KMP Compose Badge component for labels.
private struct BadgeLabelViewControllerRepresentable : VCRepresentable {
    
    @State public var label: String
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._Badge(label: label) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// Badge
///
/// A "badge" to show a either a count or label.
///
/// This is utilized in the MessageList component serving as the date separators, in the ChatList component to
/// show unread counts, and in the ChannelSettingsView for displaying admins.
///
/// - Parameters:
///    - count: The count to display in the badge.
///    This will display raw for 0-99 and will display 99+ for anything higher.
///    - label: The label to display in the badge.
///
public struct Badge: View {
    
    private var count: Int32? = nil
    private var label: String? = nil
    
    init(count: Int32? = nil) {
        self.count = count
    }
    
    init(label: String? = nil) {
        self.label = label
    }
    
    public var body: some View {
        MeasuredView(
            content: { w, h in
                if let count = count {
                    BadgeCountViewControllerRepresentable(
                        count: count,
                        measuredWidth: w,
                        measuredHeight: h
                    )
                } else if let label = label {
                    BadgeLabelViewControllerRepresentable(
                        label: label,
                        measuredWidth: w,
                        measuredHeight: h
                    )
                }
            }
        )
    }
}
                                                                         
