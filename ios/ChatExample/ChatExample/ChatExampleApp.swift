//
//  ChatExampleApp.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import SwiftUI

@main
struct ChatExampleApp: App {
    @ObservedObject var router = Router()
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router.navPath) {
                ContentView()
                    .ignoresSafeArea()
                    .navigationDestination(for: Router.Destination.self) { destination in
                        switch destination {
                        case .avatars:
                            Avatars()
                        case .badges:
                            Badges()
                        case .headers:
                            Headers()
                        }
                    }
            }.environmentObject(router)
        }
    }
}
