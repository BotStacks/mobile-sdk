//
//  WrapContentHeightSheet.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/27/24.
//

import Foundation
import SwiftUI

internal struct WrapContentHeightSheet<Content: View>: View {
    
    @State private var sheetHeight: CGFloat = .zero
    
    var content: () -> Content
    
    public var body: some View {
        content()
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

private struct InnerHeightPreferenceKey: PreferenceKey {
    static let defaultValue: CGFloat = .zero
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}
