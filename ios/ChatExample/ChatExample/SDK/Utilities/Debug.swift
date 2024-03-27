//
//  Debug.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/18/24.
//

import Foundation
import SwiftUI

extension View {
    func debugBounds(_ color: Color = Color(UIColor.magenta)) -> some View {
        return self.border(color)
    }
}
