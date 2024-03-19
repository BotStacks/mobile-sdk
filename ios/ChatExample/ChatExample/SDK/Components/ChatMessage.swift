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
    @State var onPressUser: (User) -> Void
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
            onPressUser: onPressUser,
            onLongPress: onLongPress,
            onClick: onClick
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

public struct ChatMessage: View {
    
    private var message: Message
    private var shapeDefinition: ShapeDefinition = .small
    private var showAvatar: Bool = false
    private var showTimestamp: Bool = true
    private var onPressUser: (User) -> Void = { _ in }
    private var onLongPress: () -> Void = { }
    private var onClick: ((MessageAttachment?) -> Void)? = nil
    
    init(message: Message) {
        self.message = message
    }
    
    init(message: Message, shapeDefinition: ShapeDefinition = .small, showAvatar: Bool = false, showTimestamp: Bool = true, onPressUser: @escaping (User) -> Void = { _ in }, onLongPress: @escaping () -> Void = { }, onClick: ( (MessageAttachment?) -> Void)? = nil) {
        self.message = message
        self.shapeDefinition = shapeDefinition
        self.showAvatar = showAvatar
        self.showTimestamp = showTimestamp
        self.onPressUser = onPressUser
        self.onLongPress = onLongPress
        self.onClick = onClick
    }
    
    public var body: some View {
        MeasuredView { w, h in
            ChatMessageViewControllerRepresentable(
                message: message,
                shapeDefinition: shapeDefinition,
                showAvatar: showAvatar,
                showTimestamp: showTimestamp,
                onPressUser: onPressUser,
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
}

