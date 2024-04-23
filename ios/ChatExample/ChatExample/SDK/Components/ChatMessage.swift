//
//  ChatMessage.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/18/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChatMessage component.
private struct ChatMessageViewControllerRepresentable : VCRepresentable {
    
    @State var message: Message
    @State var shapeDefinition: ShapeDefinition
    @State var showAvatar: Bool
    @State var showTimestamp: Bool
    @State var showReplies: Bool
    @State var onPressUser: (User) -> Void
    @State var openThread: () -> Void
    @State var onLongPress: () -> Void
    @State var onClick: ((MessageAttachment?) -> Void)?
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChatMessage(
            message: message,
            shapeDefinition: shapeDefinition,
            showAvatar: showAvatar,
            showTimestamp: showTimestamp,
            showReplies: showReplies,
            onPressUser: onPressUser,
            onLongPress: onLongPress,
            openThread: openThread,
            onClick: onClick
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChatMessage
///
/// Renders the contents of a given `Message` from a `ai.botstacks.sdk.state.Chat`.
/// This is used by ``MessageList`` to form the contents of a conversational chat, by properly aligning
/// messages to left or right depending on sender (left aligned for incoming and right aligned for outgoing).
///
/// - Parameters:
///  - message: The message to display.
///  - shape: corner-based shaped definition to render the "bubble" in. This defaults to `ai.botstacks.sdk.ui.theme.ShapeDefinitions.medium`
///  - showAvatar: Whether to show the associated user's avatar along with this message.
///  - showTimestamp: Whether to show the timestamp this message was sent or received.
///  - showReplies: Whether to display an interactive label for replies to this message.
///  - onPressUser: callback for when a user's avatar (when visible) is clicked.
///  - openThread: callback for when the replies label shown when showReplies is true.
///  - onLongPress: callback for when a message "bubble" is clicked.
///  - onClick: callback for when an attachment is clicked. This is utlized by [MessageList] to show images for full screen viewing.
///  
///
public struct ChatMessage: View {
    
    private var message: Message
    private var shapeDefinition: ShapeDefinition = .small
    private var showAvatar: Bool = false
    private var showTimestamp: Bool = true
    private var showReplies: Bool
    private var onPressUser: (User) -> Void = { _ in }
    private var openThread: () -> Void = { }
    private var onLongPress: () -> Void = { }
    private var onClick: ((MessageAttachment?) -> Void)? = nil
    
    init(message: Message, showReplies: Bool = true) {
        self.message = message
        self.showReplies = message.replyCount > 0 && showReplies
    }
    
    init(message: Message, shapeDefinition: ShapeDefinition = .small, showAvatar: Bool = false, showTimestamp: Bool = true, showReplies: Bool = true, onPressUser: @escaping (User) -> Void = { _ in }, openThread: @escaping () -> Void = { }, onLongPress: @escaping () -> Void = { }, onClick: ( (MessageAttachment?) -> Void)? = nil) {
        self.message = message
    
        self.shapeDefinition = shapeDefinition
        self.showAvatar = showAvatar
        self.showTimestamp = showTimestamp
        self.showReplies = message.replyCount > 0 && showReplies
        self.onPressUser = onPressUser
        self.onLongPress = onLongPress
        self.openThread = openThread
        self.onClick = onClick
    }
    
    public var body: some View {
        MeasuredView { w, h in
            ChatMessageViewControllerRepresentable(
                message: message,
                shapeDefinition: shapeDefinition,
                showAvatar: showAvatar,
                showTimestamp: showTimestamp,
                showReplies: showReplies,
                onPressUser: onPressUser,
                openThread: openThread,
                onLongPress: onLongPress,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

extension ChatMessage {
    public func shaped(_ shape: ShapeDefinition) -> ChatMessage {
        var snapshot = self
        snapshot.shapeDefinition = shape
        return snapshot
    }
    
    public func withAvatar(_ show: Bool) -> ChatMessage {
        var snapshot = self
        snapshot.showAvatar = show
        return snapshot
    }
    
    public func withTimestamp(_ show: Bool) -> ChatMessage {
        var snapshot = self
        snapshot.showTimestamp = show
        return snapshot
    }
    
    public func onPressUser(_ block: @escaping (User) -> Void ) -> ChatMessage {
        var snapshot = self
        snapshot.onPressUser = block
        return snapshot
    }
    
    public func onLongPress(_ block: @escaping () -> Void ) -> ChatMessage {
        var snapshot = self
        snapshot.onLongPress = block
        return snapshot
    }
    
    public func onClick(_ block: @escaping (MessageAttachment?) -> Void ) -> ChatMessage {
        var snapshot = self
        snapshot.onClick = block
        return snapshot
    }
    
    public func openThread(_ block: @escaping () -> Void ) -> ChatMessage {
        var snapshot = self
        snapshot.openThread = block
        return snapshot
    }
}

