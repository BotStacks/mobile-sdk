//
//  ContentView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            HStack {
                Text("Hello, world!")
                Badge(count: 3)
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
