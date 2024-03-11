//
//  Badge.swift
//  chat-sdk
//
//  Created by Brandon McAnsh on 3/7/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

public struct Badge : UIViewControllerRepresentable {

    @State public var count: Int32

    @MainActor public init(count: Int32) {
        _count = State(initialValue: count)
    }

    public func makeUIViewController(context: Context) -> UIViewController {
        ComponentsKt._Badge(count: count)
    }

    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        
    }
}
