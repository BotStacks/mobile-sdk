//
//  ProgressOverlay.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/25/24.
//

import Foundation
import SwiftUI

struct ProgressOverlay: View {
    var body: some View {
        ZStack {
            Color.black.opacity(0.5).edgesIgnoringSafeArea(.all) // Semi-transparent background
            
            ProgressView() // Default iOS spinner
                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                .scaleEffect(1.5) // Make the spinner larger
        }
    }
}
