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
                   ListRow(title: "Chat Controller Example") { router.navigate(to: .login) }
               }
               
               Section {
                   ListRow(title: "Avatar") { router.navigate(to: .avatar) }
                   ListRow(title: "Badge") { router.navigate(to: .badges) }
                   ListRow(title: "ChannelGroup") { router.navigate(to: .channelgroup) }
                   ListRow(title: "ChannelRow") { router.navigate(to: .channelrow) }
                   ListRow(title: "ChatInput") { router.navigate(to: .chatinput) }
                   ListRow(title: "ChatMessage") { router.navigate(to: .chatmessage) }
                   ListRow(title: "ChatMessagePreview") { router.navigate(to: .chatmessagepreview) }
                   ListRow(title: "Header") { router.navigate(to: .header) }
                   ListRow(title: "MessageList") { router.navigate(to: .messagelist) }
                   ListRow(title: "Spinner") { router.navigate(to: .spinner) }
                   ListRow(title: "UserProfile") { router.navigate(to: .userprofile) }
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
