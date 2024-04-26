//
//  MessageActionSheet.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/27/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose MessageActionSheet component.
private struct MessageActionSheetViewControllerRepresentable : VCRepresentable {
    
    @ObservedObject var state: BSCSDKMessageActionSheetState
        
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._MessageActionSheet(state: state._state) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// MessageActionSheet
///
///  A modal bottom sheet that allows contextual actions for a given message.
///
///  This can be utilized in conjunction with ``MessageList`` to show contextual actions for the `MessageListView.onLongPress` callback
///
///  - Parameters:
///    - state: the state for this action sheet.
///
public struct MessageActionSheet : View {
    
    @ObservedObject var state: BSCSDKMessageActionSheetState

    
    public var body: some View {
        WrapContentHeightSheet {
            MeasuredView(useFullWidth: true) { w, h in
                MessageActionSheetViewControllerRepresentable(
                    state: state,
                    measuredWidth: w,
                    measuredHeight: h
                )
            }
        }
    }
}

///
/// BSCSDKMessageActionSheetState
///
/// Wrapper observable holder around `MessageActionSheetState` that allows binding
/// presentation to sheet() ViewModifier.
///
///
///  ```
///     [...]
///     ).sheet(isPresented: state.isShowing) {
///         MessageActionSheet(state: state)
///     }
///  ```
/// To handle actions as they are initiated by a user, implement `onAction` in `onAppear` for the view the `MessageActionSheet` is attached to. (copy is internally handled with contents being added to clipboard).
///
/// ```
///     [...]
///     ).onAppear(perform: {
///         state.onAction = { m, action in
///             /// handle action here (e.g navigate, show message, etc.)
///         }
///     })
/// ```
///
class BSCSDKMessageActionSheetState : ObservableObject {

    @Published internal var _state: MessageActionSheetState = MessageActionSheetState(sheetState: nil)
    
    private var showing: Bool = false
    
    var messageForAction: Message? {
        didSet {
            _state.messageForAction = messageForAction
            self.objectWillChange.send()
            showing = messageForAction != nil
        }
    }
    
    var onAction: (Message, MessageAction) -> Void = { _, _ in } {
        didSet {
            _state.onAction = onAction
            self.objectWillChange.send()
        }
    }
    
    init() {
       let stateUpdateClosure: (KotlinBoolean) -> Void = { state in
           self.objectWillChange.send()
           self.showing = Bool(booleanLiteral: state.boolValue)
           if !self.showing {
               self.messageForAction = nil
           }
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
