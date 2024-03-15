//
//  VCRepresentable.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import SwiftUI

internal protocol VCRepresentable: UIViewControllerRepresentable {
    func makeViewController(context: Context) -> UIViewController
}

extension VCRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return makeViewController(context: context)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Implement this method if needed
    }
}
