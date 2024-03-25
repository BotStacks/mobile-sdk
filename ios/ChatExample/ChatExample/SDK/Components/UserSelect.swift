//
//  UserSelect.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/18/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose UserSelect component.
private struct UserSelectViewControllerRepresentable : VCRepresentable {
    
    @Binding var selectedUsers: [User]
    @State var canRemove: Bool = false
    @State var showAdd: Bool = false
    @State var onRemove: (User) -> Void = { _ in }
    @State var onAddSelected: () -> Void = { }
    
    @State var userComposeState: ComposeStateImplementation<NSMutableArray>
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    init(selectedUsers: Binding<[User]>, canRemove: Bool, showAdd: Bool, onRemove: @escaping (User) -> Void, onAddSelected: @escaping () -> Void, measuredWidth: Binding<CGFloat>, measuredHeight: Binding<CGFloat>) {
        _selectedUsers = selectedUsers
        self.canRemove = canRemove
        self.showAdd = showAdd
        self.onRemove = onRemove
        self.onAddSelected = onAddSelected

        _measuredWidth = measuredWidth
        _measuredHeight = measuredHeight
        
        _userComposeState = State(initialValue: ComposeStateImplementation(value: NSMutableArray(array: selectedUsers.wrappedValue)))
        
    }
    
    
    public func makeViewController(context: Context) -> UIViewController {
        return ComponentsKt._UserSelect(
            selectedUsers: userComposeState,
            canRemove: canRemove,
            showAdd: showAdd,
            onRemove: onRemove,
            onAddSelected: onAddSelected
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
    
    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        userComposeState.update(value: NSMutableArray(array: selectedUsers))
    }
}

public struct UserSelect: View {
    
    @Binding var selectedUsers: [User]
    var canRemove: Bool = false
    var showAdd: Bool = false
    var onRemove: (User) -> Void = { _ in }
    var onAddSelected: () -> Void = { }
    
    public var body: some View {
        MeasuredView(
            useFullWidth: true,
            contentPadding: EdgeInsets(top: 0, leading: 0, bottom: 4, trailing: 0)
        ) { w, h in
            UserSelectViewControllerRepresentable(
                selectedUsers: $selectedUsers,
                canRemove: canRemove,
                showAdd: showAdd,
                onRemove: onRemove,
                onAddSelected: onAddSelected,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}
