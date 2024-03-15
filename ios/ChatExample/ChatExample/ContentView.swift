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
                   ListRow(title: "Avatar") { router.navigate(to: .avatars) }
                   ListRow(title: "Badge") { router.navigate(to: .badges) }
                   ListRow(title: "ChannelRow") { router.navigate(to: .channelrow) }
                   ListRow(title: "Header") { router.navigate(to: .headers) }
                   ListRow(title: "Spinner") { router.navigate(to: .spinners) }
                   ListRow(title: "UserProfile") { router.navigate(to: .userprofiles) }
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

private struct ListRow: View {
    
    public var title: String
    public var onClick: () -> Void
    
    var body: some View {
        HStack {
            Text(title)
            Spacer()
        }.contentShape(Rectangle())
         .onTapGesture {
             onClick()
         }
    }
}

#Preview {
    ContentView()
}
