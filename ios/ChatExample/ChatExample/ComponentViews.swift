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
        ComponentView(title: "Avatars") {
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
        ComponentView(title: "Badges") {
            HStack {
                Badge(count: 3)
                Badge(label: "Admin")
            }
        }
    }
}

struct Headers: View {
    
    @State private var headerState: HeaderState = HeaderState.init(showSearch: true, showSearchClear: true)
    
    var body: some View {
        ComponentView(title: "Headers") {
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
        .navigationBarTitle(Text(""), displayMode: .inline) // Hide navigation bar title
        .navigationBarBackButtonHidden()
    }
}
