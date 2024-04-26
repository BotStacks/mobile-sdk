//
//  ChatMessagePreview.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/18/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChatMessagePreview component.
private struct ChatMessagePreviewViewControllerRepresentable : VCRepresentable {
    
    @State var chat: Chat
    @State var onClick: () -> Void
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChatMessagePreview(
            chat: chat,
            onClick: onClick
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChatMessagePreview
///
/// Renders a "preview" for a given chat. This is based on the last message, if any, that was either
/// sent or received in the chat. Attachments will be rendered as well.
///
/// - Parameters:
///  - chat: The chat to extract info from for the preview.
///  - onClick: callback when the contents is clicked.
///  
public struct ChatMessagePreview: View {
    
    private var chat: Chat
    private var onClick: () -> Void
    
    
    init(chat: Chat, onClick: @escaping () -> Void) {
        self.chat = chat
        self.onClick = onClick
    }
    
    public var body: some View {
        MeasuredView(useFullWidth: true) { w, h in
            ChatMessagePreviewViewControllerRepresentable(
                chat: chat,
                onClick: onClick,
                measuredWidth: w, 
                measuredHeight: h
            )
        }
    }
}
