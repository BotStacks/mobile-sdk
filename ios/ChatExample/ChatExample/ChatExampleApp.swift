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
                        case .avatars: Avatars()
                        case .badges: Badges()
                        case .channelrow: ChannelRows()
                        case .channelgroup: ChannelGroups()
                        case .chatinput: ChatInputExample()
                        case .headers: Headers()
                        case .spinners: Spinners()
                        case .userprofiles: UserProfiles()
                        }
                    }
            }.environmentObject(router)
        }
    }
}

extension UINavigationController: UIGestureRecognizerDelegate {
    override open func viewDidLoad() {
        super.viewDidLoad()
        interactivePopGestureRecognizer?.delegate = self
    }

    public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        return viewControllers.count > 1
    }

    // To make it works also with ScrollView
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        true
    }
}
