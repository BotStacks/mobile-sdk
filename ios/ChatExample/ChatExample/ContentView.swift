//
//  ContentView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import SwiftUI
import BotStacks_ChatSDK

struct ContentView: View {
    @EnvironmentObject var router: Router
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Header()
            ComponentShowCase()
            Spacer()
        }
        .frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .topLeading
        )
        .navigationBarTitle(Text(""), displayMode: .inline) // Hide navigation bar title
    }
}

struct ComponentShowCase: View {
    @EnvironmentObject var router: Router
    
    var body: some View {
        VStack {
           List {
               Section {
                   HStack {
                       Text("Avatars")
                       Spacer()
                   }.contentShape(Rectangle())
                    .onTapGesture {
                        router.navigate(to: .avatars)
                    }
                   
                   HStack {
                       Text("Badges")
                       Spacer()
                   }.contentShape(Rectangle())
                    .onTapGesture {
                        router.navigate(to: .badges)
                    }
                   
                   HStack {
                       Text("Headers")
                       Spacer()
                   }.contentShape(Rectangle())
                    .onTapGesture {
                        router.navigate(to: .headers)
                    }
               } header: {
                   Text("Components")
               }
            }
        }
        .frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .top
        )
    }
}

#Preview {
    ContentView()
}
