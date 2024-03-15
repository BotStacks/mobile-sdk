//
//  ChannelRow.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

/// The VC representable that abstracts away our KMP Compose ChannelRow component.
private struct ChannelRowViewControllerRepresentable : VCRepresentable {
    
    @State public var chat: Chat? = nil
    @State public var showMemberPreview: Bool = false
    
    @State public var imageUrls: [String] = []
    @State public var title: String
    @State public var titleFontStyle: FontStyle? = nil
    @State public var titleColor: UIColor? = nil
    
    @State public var subtitle: String? = nil
    @State public var subtitleFontStyle: FontStyle? = nil
    @State public var subtitleColor: UIColor? = nil
    
    @State public var onClick: () -> Void
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._ChannelRow(
            chat: chat,
            showMemberPreview: showMemberPreview,
            imageUrls: imageUrls,
            title: title,
            titleFontStyle: titleFontStyle,
            titleColor: titleColor,
            subtitle: subtitle,
            subtitleFontStyle: subtitleFontStyle,
            subtitleColor: subtitleColor,
            onClick: onClick
        ) { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// ChannelRow
///
/// Used to render information about a `Chat`.
///
/// This is utilized in the [ChannelGroup] within [UserDetailsView] to show channels the current user has in common with any other user.
///
///  This can either take in a `Chat` directly or be supplied title/subtitle/imageUrls,
///
/// - Parameters:
///     - chat: The channel to display.
///     - showMemberPreview: Whether to show a member preview as the subtitle (when used with chat).
///     - titleFontStyle : ``FontStyle`` for the title (top text).
///     - titleColor: ``SwiftUI.Color`` for the title.
///     - subtitleFontStyle: ``FontStyle`` for the subtitle (bottom text).
///     - subtitleColor: `Color` for the subtitle.
///     - onClick: callback called when this row is clicked
///     - imageUrls: [User] image urls of members of the channel.
///     - title: Text to display in title slot (top text).
///     - subtitle: Optional text to display in subtitle slot (bottom text).
///
public struct ChannelRow: View {
    
    private var chat: Chat? = nil
    private var showMemberPreview: Bool = false
    
    private var imageUrls: [String] = []
    private var title: String = ""
    private var titleFontStyle: FontStyle? = nil
    private var titleColor: UIColor? = nil
    
    private var subtitle: String? = nil
    private var subtitleFontStyle: FontStyle? = nil
    private var subtitleColor: UIColor? = nil
    
    public var onClick: () -> Void
    
    init(imageUrls: [String], title: String, titleFontStyle: FontStyle? = nil, titleColor: Color? = nil, subtitle: String? = nil, subtitleFontStyle: FontStyle? = nil, subtitleColor: Color? = nil, onClick: @escaping () -> Void) {
        

        self.imageUrls = imageUrls
        self.title = title
        self.titleFontStyle = titleFontStyle
        if let titleColor = titleColor {
            self.titleColor = UIColor(titleColor)
        }
        self.subtitle = subtitle
        self.subtitleFontStyle = subtitleFontStyle
        if let subtitleColor = subtitleColor {
            self.subtitleColor = UIColor(subtitleColor)
        }
        self.onClick = onClick
    }
    
    init(chat: Chat, showMemberPreview: Bool = false, onClick: @escaping () -> Void) {
        self.chat = chat
        self.showMemberPreview = showMemberPreview
        self.onClick = onClick
    }
    
    
    public var body: some View {
        MeasuredView(useFullWidth: true) { w, h in
            ChannelRowViewControllerRepresentable(
                chat: chat,
                showMemberPreview: showMemberPreview,
                title: title,
                titleFontStyle: titleFontStyle,
                titleColor: titleColor,
                subtitle: subtitle,
                subtitleFontStyle: subtitleFontStyle,
                subtitleColor: subtitleColor,
                onClick: onClick,
                measuredWidth: w,
                measuredHeight: h
            )
        }
    }
}
