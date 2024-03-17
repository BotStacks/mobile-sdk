//
//  ChatInput.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChatInput component.
private struct ChatInputViewControllerRepresentable : VCRepresentable {
    
    @State var chat: Chat
    @State var onMedia: () -> Void
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChatInput(chat: chat, onMedia: onMedia) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChatInput
///
/// Text input that handles the sending of messages to a given `Chat` when the send button is pressed.
/// This is generally used for ``ChatExample/MessageList`` as there is handling for an attachment sheet that will present from the callback `onMedia`.
///
/// - Parameters:
///  - chat: The chat associated with this input
///  - onMedia: when the media button is pressed.
///  
public struct ChatInput : View {
    
    private var chat: Chat
    private var onMedia: () -> Void
    
    public init(chat: Chat, onMedia: @escaping () -> Void) {
        self.chat = chat
        self.onMedia = onMedia
    }
    
    public var body: some View {
        MeasuredView(useFullWidth: true) { w, h in
            ChatInputViewControllerRepresentable(
                chat: chat,
                onMedia: onMedia,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}
