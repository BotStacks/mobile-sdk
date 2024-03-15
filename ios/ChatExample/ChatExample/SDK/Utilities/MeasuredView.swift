//
//  MeasuredView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/11/24.
//

import Foundation
import SwiftUI

internal struct MeasuredView<Content: View>: View {
    
    @State public var measuredWidth: CGFloat = 1000.0
    @State var measuredHeight: CGFloat = 1000.0
    
    var contentPadding: EdgeInsets = EdgeInsets()
        
    var useFullWidth: Bool = false
    var useFullHeight: Bool = false
    var content: (Binding<CGFloat>, Binding<CGFloat>) -> Content
    
    init(
        useFullWidth: Bool = false,
        useFullHeight: Bool = false,
        contentPadding: EdgeInsets = EdgeInsets(),
        @ViewBuilder content: @escaping (Binding<CGFloat>, Binding<CGFloat>) -> Content
    ) {
        self.useFullWidth = useFullWidth
        self.useFullHeight = useFullHeight
        self.contentPadding = contentPadding
        self.content = content
    }
    
    var body: some View {
        let width = (measuredWidth / UIScreen.main.scale) + contentPadding.leading + contentPadding.trailing
        let height = (measuredHeight / UIScreen.main.scale) + contentPadding.top + contentPadding.bottom
        
        content($measuredWidth, $measuredHeight)
            .modifier(ConditionalFrame(useFullWidth: useFullWidth, useFullHeight: useFullHeight, measuredWidth: width, measuredHeight: height))
    }
}

private struct ConditionalFrame: ViewModifier {
    let useFullWidth: Bool
    let useFullHeight: Bool
    let measuredWidth: CGFloat
    let measuredHeight: CGFloat
    
    func body(content: Content) -> some View {
        if useFullWidth && useFullHeight {
            content.frame(
                maxWidth: .infinity,
                maxHeight: .infinity
            )
        } else if useFullWidth {
            content
                .frame(maxWidth: .infinity)
                .frame(height: measuredHeight)
        } else if useFullHeight {
            content
                .frame(maxHeight: .infinity)
                .frame(width: measuredWidth)
        } else {
            content.frame(width: measuredWidth, height: measuredHeight)
        }
    }
}