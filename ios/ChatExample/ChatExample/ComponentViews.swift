//
//  ComponentViews.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

struct Avatars: View {
    var body: some View {
        ComponentView(title: "Avatar") {
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

struct Badges: View {
    var body: some View {
        ComponentView(title: "Badge") {
            HStack {
                Badge(count: 3)
                Badge(label: "Admin")
            }
        }
    }
}

struct ChannelRows: View {
    var body: some View {
        ComponentView(title: "ChannelRow") {
            ChannelRow(imageUrls: [], title: "iOS Devs", subtitle: "27 members") {
                print("iOS Devs clicked")
            }
            ChannelRow(
                imageUrls: [
                    "https://randomuser.me/api/portraits/men/81.jpg",
                    "https://randomuser.me/api/portraits/men/84.jpg"
                ],
                title: "Dudes",
                titleColor: Color.green,
                subtitle: "John, Dan"
            ) {
                print("dudes clicked")
            }
        }
    }
}

struct Headers: View {
    
    @State private var headerState: HeaderState = HeaderState.init(showSearch: true, showSearchClear: true)
    
    var body: some View {
        ComponentView(title: "Header") {
            Spacer(minLength: 50)
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
            
            Text("With menu")
                .font(.callout)
            Header()
                .title("Home")
                .menu {
                    Menu {
                        Button("BotStacks is 🔥", action: { })
                    } label: {
                        Image(systemName: "ellipsis")
                            .tint(Color.black)
                    }
                }
            
            Text("With back navigation")
                .font(.callout)
            Header()
                .backClicked { print("back clicked") }
            
            Text("With endAction")
                .font(.callout)
            Header()
                .title("Settings")
                .backClicked { print("back clicked") }
                .withEndAction(.next(onClick: { print("Next clicked") }))
            
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

struct Spinners: View {
    var body: some View {
        ComponentView(title: "Spinner") {
            Spinner()
        }
    }
}

struct UserProfiles: View {
    
    private var user1: User
    private var user2: User
    
    init() {
        self.user1 = User(id: UUID().uuidString)
        self.user1.username = "jon_doe"
        self.user1.displayName = "Jon Doe"
        self.user1.avatar = "https://randomuser.me/api/portraits/men/81.jpg"
        
        self.user2 = User(id: UUID().uuidString)
        self.user2.username = "jane_doe"
        self.user2.displayName = "Jane Doe"
        self.user2.avatar = "https://randomuser.me/api/portraits/women/21.jpg"
    }
    
    var body: some View {
        ComponentView(title: "UserProfile") {
            VStack {
                HStack {
                    UserProfile(user: user1)
                    UserProfile(user: user2)
                }
            }
        }
    }
}

private struct ComponentView<Content: View>: View {
    
    @EnvironmentObject var router: Router
    
    var title: String
    var content: () -> Content
    
    init(title: String, @ViewBuilder content: @escaping () -> Content) {
        self.title = title
        self.content = content
    }
    
    var body: some View {
        VStack {
            Header()
                .title(title)
                .backClicked {
                    router.navigateBack()
                }
            
            ScrollView {
                content()
            }
        }.frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .topLeading
        )
        .ignoresSafeArea()
        .navigationBarTitle(Text(""), displayMode: .inline) // Hide navigation bar title
        .navigationBarBackButtonHidden()
    }
}