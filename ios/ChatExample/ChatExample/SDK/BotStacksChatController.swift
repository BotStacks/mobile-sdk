//
//  BotStacksChatController.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/16/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose Badge component for counts.
private struct ChatControllerRepresentable : VCRepresentable {
    
    @State var onLogout: () -> Void

    public func makeViewController(context: Context) -> UIViewController {
        ChatControllerKt._ChatController(onLogout: onLogout)
    }
}

public struct BotStacksChatController: View {
    
    private var onLogout: () -> Void
    
    init(onLogout: @escaping () -> Void) {
        self.onLogout = onLogout
    }
    
    public var body: some View {
        ChatControllerRepresentable(onLogout: onLogout)
    }
}
