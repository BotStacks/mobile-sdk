//
//  Avatar.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/11/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose Avatar component.
private struct AvatarViewControllerRepresentable : VCRepresentable {
    
    @State public var size: AvatarSize
    @State public var type: AvatarType
    @State public var isSelected: Bool
    @State public var isRemovable: Bool
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._Avatar(size: size, type: type, isSelected: isSelected, isRemovable: isRemovable) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// Avatar
///
/// Renders the display image in a bordered circle at the specified size. This can render user images as well as channel images.
///
/// - Parameters:
///    - url: The user image to render.
///    - urls: To render a channel avatar, provide the [String] urls for the members. (NOTE: only 4 are drawn)
///    - isSelected: Whether to draw this in a selected state.
///    - isRemovable: Whether to draw this in a removable state.
///
public struct Avatar: View {
    
    private var size: AvatarSize = AvatarSizeSmall.shared
    
    private var onlineStatus: OnlineStatus = OnlineStatus.unknown
    private var empty: Ui_graphicsPainter? = nil
    
    private var type: AvatarType? = nil
    private var isSelected: Bool = false
    private var isRemovable: Bool = false
    
    public init(url: String, isSelected: Bool = false, isRemovable: Bool = false) {
        self.type = AvatarTypeUser(url: url, status: OnlineStatus.unknown, empty: nil)
        self.isSelected = isSelected
        self.isRemovable = isRemovable
    }
    
    public init(urls: [String], isSelected: Bool = false, isRemovable: Bool = false) {
        self.type = AvatarTypeChannel(urls: urls, empty: nil)
        self.isSelected = isSelected
        self.isRemovable = isRemovable
    }
    
    public var body: some View {
        MeasuredView(
            contentPadding: EdgeInsets(top: 0, leading: 0, bottom: onlineStatus != OnlineStatus.unknown ? 4 : 0, trailing: 0),
            content: { w, h in
                if let type = type {
                    AvatarViewControllerRepresentable(
                        size: size,
                        type: type,
                        isSelected: isSelected,
                        isRemovable: isRemovable,
                        measuredWidth: w,
                        measuredHeight: h
                    )
                }
            }
        )
    }
}

extension Avatar {
    ///
    /// Mutates the avatar to render for a given user image URL
    ///
    /// - Parameters:
    ///     - url: The remote image URL to load.
    ///
    public func userUrl(_ url: String) -> Avatar {
        var snapshot = self
        snapshot.type = AvatarTypeUser(url: url, status: onlineStatus, empty: empty)
        return snapshot
    }
    
    ///
    /// Mutates the Avatar to render an `OnlineStatus` indicator (if known) for a given user avatar.
    ///
    ///  NOTE: This will have no effect on Channel Avatars.
    ///
    /// - Parameters:
    ///     - onlineStatus: The online status for the user.
    ///
    public func onlineStatus(_ onlineStatus: OnlineStatus) -> Avatar {
        var snapshot = self
        snapshot.onlineStatus = onlineStatus
        switch (type) {
        case let user as AvatarTypeUser:
            snapshot.type = AvatarTypeUser(url: user.url, status: onlineStatus, empty: user.empty)
            
        default: break
        }
        
        return snapshot
    }
    
    ///
    /// Updates an `Avatar` for a new user image URL and online status pairing.
    ///
    ///  Combines `userUrl` and `onlineStatus` extensions.
    ///
    ///
    /// - Parameters:
    ///     - url: The remote image URL to load.
    ///     - onlineStatus: The online status for the user.
    ///
    public func user(_ url: String, onlineStatus: OnlineStatus = OnlineStatus.unknown) -> Avatar {
        var snapshot = self
        snapshot.onlineStatus = onlineStatus
        snapshot.type = AvatarTypeUser(url: url, status: onlineStatus, empty: empty)
        return snapshot
    }
    
    ///
    /// Updates this `Avatar` for a new `User`.
    ///
    /// - Parameters:
    ///     - user: The `User` to reflect in the `Avatar`.
    ///
    public func user(_ user: User) -> Avatar {
        var snapshot = self
        snapshot.type = AvatarTypeUser(url: user.avatar, status: user.status, empty: empty)
        return snapshot
    }
    
    ///
    /// Overrides the empty state if a given URL fails to load.
    ///
    /// - Parameters:
    ///  - image: A `UIImage` to use as the empty state.
    ///
    public func ifEmpty(_ image: UIImage?) -> Avatar {
        var snapshot = self
        switch (type) {
        case let user as AvatarTypeUser:
            snapshot.type = BSKAvatarType().user(url: user.url, status: user.status, empty: image)
            
        case let channel as AvatarTypeChannel:
            snapshot.type = BSKAvatarType().channel(urls: channel.urls, empty: image)

        default: break
        }
        return snapshot
    }
    
    ///
    /// Overrides the empty state if a given URL fails to load.
    ///
    /// - Parameters:
    ///  - image: A `Image` to use as the empty state.
    ///
    public func ifEmpty(_ image: Image?) -> Avatar {
        var snapshot = self
        let empty = imageFromView(image, size: CGSize(width: 100.0, height: 100.0))
        switch (type) {
        case let user as AvatarTypeUser:
            snapshot.type = BSKAvatarType().user(url: user.url, status: user.status, empty: empty)
            
        case let channel as AvatarTypeChannel:
            snapshot.type = BSKAvatarType().channel(urls: channel.urls, empty: empty)

        default: break
        }
        return snapshot
    }
    
    private func imageFromView<Content: View>(_ view: Content, size: CGSize) -> UIImage {
        let controller = UIHostingController(rootView: view)
        controller.view.bounds = CGRect(origin: .zero, size: size)
        let renderer = UIGraphicsImageRenderer(size: size)
        let uiImage = renderer.image { _ in
            controller.view.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
        }
        return uiImage
    }
}
