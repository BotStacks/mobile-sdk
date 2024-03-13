//
//  ContentView.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import SwiftUI
import BotStacks_ChatSDK

struct ContentView: View {
    var body: some View {
        VStack(alignment: .leading) {
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
    var body: some View {
        ScrollView {
            VStack {
                Text("Component Showcase")
                    .font(.largeTitle)
                
                Avatars()
                Divider()
                Badges()
                Divider()
                Headers()
            }.frame(
                maxWidth: .infinity,
                maxHeight: .infinity,
                alignment: .top
            )
        }
    }
}

private struct Avatars: View {
    var body: some View {
        VStack {
            Text("Avatar")
                .font(.headline)
            HStack {
                Avatar(url: "https://randomuser.me/api/portraits/men/81.jpg")
                Avatar(url: "https://randomuser.me/api/portraits/men/80.jpg")
                    .onlineStatus(OnlineStatus.online)
                
                Avatar(url: "https://randomuser.me/api/portraits/men/-1.jpg")
                    .onlineStatus(OnlineStatus.offline)
                    .ifEmpty(Image(systemName: "globe"))
            }
        }
    }
}

private struct Badges: View {
    var body: some View {
        ComponentView(title: "Badges") {
            HStack {
                Badge(count: 3)
                Badge(label: "Admin")
            }
        }
    }
}

private struct Headers: View {
    
    @State private var headerState: HeaderState = HeaderState.init(showSearch: true, showSearchClear: true)
    
    var body: some View {
        ComponentView(title: "Headers") {
            Text("Using title string")
                .font(.callout)
                .frame(alignment: .leading)
            
            Header()
                .title("Settings")
            
            Text("Using title slot")
                .font(.callout)
            
            Header()
                .title {
                    Text("User Details")
                        .font(.headline)
                }
            
            Text("Using icon")
                .font(.callout)
            Header()
                .icon(UIImage.init(systemName: "globe")!)
            
            Text("With back navigation")
                .font(.callout)
            Header()
                .backClicked { print("back clicked") }
            
            Text("With multiple actions and searchability")
                .font(.callout)
            Header()
                .withState(headerState)
                .backClicked {
                    if headerState.searchActive {
                        headerState.searchActive = false
                        print("closing search")
                    } else {
                        print("back click")
                    }
                }
                .addClicked { print("add clicked") }
                .composeClicked { print("compose clicked") }
        }
    }
}

private struct ComponentView<Content: View>: View {
    
    var title: String
    var content: () -> Content
    
    init(title: String, @ViewBuilder content: @escaping () -> Content) {
        self.title = title
        self.content = content
    }
    
    var body: some View {
        VStack {
            Text(title)
                .font(.headline)
            
            content()
        }
    }
}

#Preview {
    ContentView()
}
