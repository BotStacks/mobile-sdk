//
//  MediaActionSheet.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/26/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChatInput component.
private struct MediaActionSheetViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKMediaActionSheetState
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._MediaActionSheet(state: state._state) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// MediaActionSheet
///
/// A modal bottom sheet that displays attachments that can be sent in a chat.
///
///  - Parameters:
///    - state: the state for this action sheet.
///
public struct MediaActionSheet : View {
    
    @ObservedObject var state: BSCSDKMediaActionSheetState
    
    @State private var sheetHeight: CGFloat = .zero
    
    public var body: some View {
        MeasuredView(useFullWidth: true) { w, h in
            MediaActionSheetViewControllerRepresentable(
                state: state,
                measuredWidth: w,
                measuredHeight: h
            )
            .overlay {
                GeometryReader { geometry in
                    Color.clear.preference(key: InnerHeightPreferenceKey.self, value: geometry.size.height)
                }
            }
            .onPreferenceChange(InnerHeightPreferenceKey.self) { newHeight in
                sheetHeight = newHeight
            }
            .presentationDetents([.height(sheetHeight)])
        }
    }
    
    struct InnerHeightPreferenceKey: PreferenceKey {
        static let defaultValue: CGFloat = .zero
        static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
            value = nextValue()
        }
    }
}

///
/// BSCSDKMediaActionSheetState
///
/// Wrapper observable holder around `MediaActionSheetState` that allows binding
/// presentation to sheet() ViewModifier.
///
///  ```
///     [...]
///     ).sheet(isPresented: state.isShowing) {
///         MediaActionSheet(state: state)
///     }
///  ```
///
class BSCSDKMediaActionSheetState : ObservableObject {

    
    private var chat: Chat
    @Published internal var _state : MediaActionSheetState
    
    private var showing: Bool = false
    
    init(chat: Chat) {
        self.chat = chat
        self._state = MediaActionSheetState(chat: chat, sheetState: nil)
        
        let stateUpdateClosure: (KotlinBoolean) -> Void = { state in
            self.objectWillChange.send()
            self.showing = Bool(booleanLiteral: state.boolValue)
        }
        
        _state.onStateChange = stateUpdateClosure
    }
    
    var isShowing: Binding<Bool> {
        Binding<Bool>(
            get: { self.showing },
            set: {
                self.objectWillChange.send()
                self.showing = $0
            }
        )
    }
    
    func show() {
        objectWillChange.send()
        _state.show { error in
            if let error = error {
                print("error \(error)")
            }
        }
    }
    
    func hide() {
        objectWillChange.send()
        _state.hide { error in
            if let error = error {
                print("error \(error)")
            }
        }
    }
}
