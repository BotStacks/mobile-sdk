//
//  Badge.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI


struct BadgeViewControllerRepresentable : UIViewControllerRepresentable {
    
    @State public var count: Int32
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat

    public func makeUIViewController(context: Context) -> UIViewController {
        print("makeUIViewController")
        let vc = ComponentsKt._Badge(count: count) { w, h in
            print("makeUIViewController:: measured", w, h)
            measuredWidth = CGFloat(w)
            measuredHeight = CGFloat(h)
        }
        
        return vc
    }

    public func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

public struct Badge: View {
    
    let count: Int32
    @State var measuredWidth: CGFloat = 1000.0
    @State var measuredHeight: CGFloat = 1000.0
    
    public var body: some View {
        let _ = print("w", measuredWidth, "h", measuredHeight)
        VStack {
            BadgeViewControllerRepresentable(
                count: count, measuredWidth: $measuredWidth, measuredHeight: $measuredHeight
            ).frame(width: measuredWidth / UIScreen.main.scale, height: measuredHeight / UIScreen.main.scale)
        }
    }
}
                                                                         
