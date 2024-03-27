//
//  Spinner.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

/// The VC representable that abstracts away our KMP Compose Spinner component.
private struct SpinnerViewControllerRepresentable : VCRepresentable {
    
    @Binding var measuredWidth: CGFloat
    @Binding var measuredHeight: CGFloat
    
    public func makeViewController(context: Context) -> UIViewController {
        ComponentsKt._Spinner { w, h in
            measuredWidth = CGFloat(truncating: w)
            measuredHeight = CGFloat(truncating: h)
        }
    }
}

///
/// Spinner
///
/// Themed spinner, colored with ``BotStacksChatSDK.Colors.primary``.
public struct Spinner: View {
    public var body: some View {
        MeasuredView(content: { w, h in
            SpinnerViewControllerRepresentable(measuredWidth: w, measuredHeight: h)
        })
    }
}
