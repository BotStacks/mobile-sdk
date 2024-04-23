//
//  MessageList.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/21/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose MessageList component.
private struct MessageListViewControllerRepresentable : VCRepresentable {
    
    @State var chat: Chat
    @State var header: (() -> UIView)? = nil
    @State var contentHeader: (() -> UIView)? = nil
    @State var emptyState: (() -> UIView)? = nil
    @State var onPressUser: (User) -> Void = { _ in }
    @State var onLongPress: (Message) -> Void = { _ in }
    @State var openThread: (Message) -> Void = { _ in }
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._MessageList(
            chat: chat,
            header: header,
            contentHeader: contentHeader,
            emptyState: emptyState,
            onPressUser: onPressUser,
            openThread: openThread,
            onLongPress: onLongPress
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// MessageList
///
///  A conversational component displaying the messages for a given `Chat` in an infinite scrolling list.
///
///   - Parameters:
///    - chat: The chat to render messages for.
///    - onPressUser: callback when a user's avatar is pressed when visible next to a given message.
///    (only applicable in multi user based chats).
///    - onLongPress: callback when a message is long pressed. This can be utilized with ``ChatExample/MessageActionSheet`` to show contextual actions.
///    
public struct MessageList: View {
    
    private var chat: Chat
    private var header: UIView? = nil
    private var contentHeader: UIView? = nil
    private var emptyState: UIView? = nil
    private var onPressUser: (User) -> Void = { _ in }
    private var onLongPress: (Message) -> Void = { _ in }
    private var openThread: (Message) -> Void = { _ in }
    
    init(chat: Chat, /*header: UIView? = nil,*/ contentHeader: UIView? = nil, emptyState: UIView? = nil, onPressUser: @escaping (User) -> Void = { _ in }, onLongPress: @escaping (Message) -> Void, openThread: @escaping (Message) -> Void) {
        self.chat = chat
//        self.header = header
        self.contentHeader = contentHeader
        self.emptyState = emptyState
        self.onPressUser = onPressUser
        self.onLongPress = onLongPress
        self.openThread = openThread
    }
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            MessageListViewControllerRepresentable(
                chat: chat, 
                header: header != nil ? { header! } : nil,
                contentHeader: contentHeader != nil ? { contentHeader! } : nil,
                emptyState: emptyState != nil ? { emptyState! } : nil,
                onPressUser: onPressUser,
                onLongPress: onLongPress,
                openThread: openThread,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

extension MessageList {
//    public func withHeader(_ block: () -> Header) -> MessageList {
//        var snapshot = self
//        let view = convertToUIView(block())
//        snapshot.header = { view }()
//        return snapshot
//    }
    
    public func whenEmpty(_ block: () -> any View) -> MessageList {
        var snapshot = self
        let view = convertToUIView(block())
        snapshot.emptyState = { view }()
        return snapshot
    }
}
