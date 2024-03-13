//
//  Header.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/12/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose Header component.
private struct HeaderViewControllerRepresentable : UIViewControllerRepresentable {
    
    @State public var state: HeaderState = HeaderState.init(showSearch: false, showSearchClear: false)
    @State public var title: String? = nil
    @State public var titleSlot: (() -> UIView)? = nil
    @State public var icon: UIImage? = nil
    @State public var onSearchClick: (() -> Void)? = nil
    @State public var onAdd: (() -> Void)? = nil
    @State public var onCompose: (() -> Void)? = nil
    @State public var onBackClicked: (() -> Void)? = nil
    @State public var endAction: (() -> UIView)? = nil
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeUIViewController(context: Context) -> UIViewController {
        ComponentsKt._Header(
            state: state,
            title: title,
            titleSlot: titleSlot,
            icon: icon,
            onSearchClicked: onSearchClick,
            onAdd: onAdd,
            onCompose: onCompose,
            onBackClicked: onBackClicked,
            endAction: endAction
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }


    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

///
/// Header
///
/// A top bar that can be utilized together with a content view to create a screen.
///
/// - Parameters:
///     - title: The title string to display.
///     - icon: An optional icon to display when up navigation is not present.
///     - state: The state for the header.
///     - onSearchClick: callback for when search icon is clicked if visible via state.
///     - onAdd: callback for when the add option is clicked. Providing this callback will enable an add action to show.
///     - onCompose: callback when the compose option is clicked. Providing this callback will enable a compose action to show.
///     - onBackClicked: callback for when up navigation is clicked. Providing this callback will enable up navigation to show.
///     - endAction: optional slot for an additional action at the end.
///     
public struct Header: View {
    
    private var state: HeaderState = HeaderState.init(showSearch: false, showSearchClear: false)
    private var title: String? = nil
    private var titleSlot: UIView? = nil
    private var icon: UIImage? = nil
    private var onSearchClick: (() -> Void)? = nil
    private var onAdd: (() -> Void)? = nil
    private var onCompose: (() -> Void)? = nil
    private var onBackClicked: (() -> Void)? = nil
    private var endAction: UIView? = nil
    
    init(state: HeaderState = HeaderState.init(showSearch: false, showSearchClear: false), title: String? = nil, icon: UIImage? = nil, onSearchClick: ( () -> Void)? = nil, onAdd: ( () -> Void)? = nil, onCompose: ( () -> Void)? = nil, onBackClicked: ( () -> Void)? = nil, endAction: UIView? = nil) {
        self.state = state
        self.title = title
        self.icon = icon
        self.onSearchClick = onSearchClick
        self.onAdd = onAdd
        self.onCompose = onCompose
        self.onBackClicked = onBackClicked
        self.endAction = endAction
    }
    
    public var body: some View {
        MeasuredView(
            useFullWidth: true,
            content: { w, h in
                HeaderViewControllerRepresentable(
                    state: state,
                    title: title,
                    titleSlot: titleSlot != nil ? { titleSlot! } : nil,
                    icon: icon,
                    onSearchClick: onSearchClick,
                    onAdd: onAdd,
                    onCompose: onCompose,
                    onBackClicked: onBackClicked,
                    endAction: endAction != nil ? { endAction! } : nil,
                    measuredWidth: w,
                    measuredHeight: h
                )
            }
        )
    }
}

extension Header {
    public func title(_ block: () -> any View) -> Header {
        var snapshot = self
        let view = convertToUIView(block())
        snapshot.titleSlot = { view }()
        return snapshot
    }
    
    public func title(_ title: String) -> Header {
        var snapshot = self
        snapshot.title = title
        return snapshot
    }
    
    public func icon(_ icon: UIImage) -> Header {
        var snapshot = self
        snapshot.icon = icon
        return snapshot
    }
    
    public func backClicked(_ callback: @escaping () -> Void) -> Header {
        var snapshot = self
        snapshot.onBackClicked = callback
        return snapshot
    }
    
    public func addClicked(_ callback: @escaping () -> Void) -> Header {
        var snapshot = self
        snapshot.onAdd = callback
        return snapshot
    }
    
    public func composeClicked(_ callback: @escaping () -> Void) -> Header {
        var snapshot = self
        snapshot.onCompose = callback
        return snapshot
    }
    
    public func searchClicked(_ callback: @escaping () -> Void) -> Header {
        var snapshot = self
        snapshot.onSearchClick = callback
        return snapshot
    }
    
    public func withState(_ state: HeaderState) -> Header {
        var snapshot = self
        snapshot.state = state
        return snapshot
    }
    
    public func withSearchVisible(_ showSearch: Bool, _ showClear: Bool = false) -> Header {
        var snapshot = self
        snapshot.state = HeaderState(showSearch: showSearch, showSearchClear: showSearch)
        return snapshot
    }
}

private func removeBackground(_ view: UIView) -> UIView {
    view.backgroundColor = .clear
    return view
}

private func convertToUIView<V: View>(_ swiftUIView: V) -> UIView {
    let controller = UIHostingController(rootView: swiftUIView)
    let view = controller.view!
    controller.view.backgroundColor = .clear
    return view
}
