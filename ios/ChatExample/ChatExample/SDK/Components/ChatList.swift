//
//  ChatList.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/18/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChatList component.
private struct ChatListViewControllerRepresentable : VCRepresentable {
    
    @State var header: (() -> UIView)?
    @State var emptyState: (() -> UIView)?
    @State var filter: (Chat) -> Bool = { _ in true }
    @State var onChatClicked: (Chat) -> Void
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChatList(
            header: header,
            emptyState: emptyState,
            filter: { chat in
                let result = filter(chat)
                return KotlinBoolean(booleanLiteral: result)
            },
            onChatClicked: onChatClicked
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChatList
///
/// Renders a given list of `Chat` in an infinite scrolling list. Each chat will render a preview of
/// it using ``ChatMessagePreview``.
///
///  - Parameters:
///   - header: Optional header to display, fixed, at the top of the list. Normally this would be a ``Header``.
///   - emptyState: Optional UI state for when there is no chats available. See `ai.botstacks.sdk.ui.theme.Assets` and `ai.botstacks.sdk.ui.theme.EmptyScreenConfig`.
///   - filter: predicate to filter the chats that are loaded. This is generally done from search in ``ChatExample/Header``.
///   - onChatClicked: callback for when a [Chat] in the list is clicked.
///
public struct ChatList: View {
    
    private var header: UIView? = nil
    private var emptyState: UIView? = nil
    private var filter: (Chat) -> Bool = { _ in true }
    private var onChatClicked: (Chat) -> Void
    
    init(/*header: UIView? = nil,*/ emptyState: UIView? = nil, filter: @escaping (Chat) -> Bool = { _ in true }, onChatClicked: @escaping (Chat) -> Void) {
//        self.header = header
        self.emptyState = emptyState
        self.filter = filter
        self.onChatClicked = onChatClicked
    }
    
    public var body: some View {
        MeasuredView(useFullWidth: true, useFullHeight: true) { w, h in
            ChatListViewControllerRepresentable(
                header: header != nil ? { header! } : nil,
                emptyState: emptyState != nil ? { emptyState! } : nil,
                onChatClicked: onChatClicked,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}

extension ChatList {
    //    public func withHeader(_ block: () -> Header) -> ChatList {
    //        var snapshot = self
    //        let view = convertToUIView(block())
    //        snapshot.header = { view }()
    //        return snapshot
    //    }
        
    public func whenEmpty(_ block: () -> any View) -> ChatList {
        var snapshot = self
        let view = convertToUIView(block())
        snapshot.emptyState = { view }()
        return snapshot
    }
    
    public func filter(_ predicate: @escaping (Chat) -> Bool) -> ChatList {
        var snapshot = self
        snapshot.filter = predicate
        return snapshot
    }
}
