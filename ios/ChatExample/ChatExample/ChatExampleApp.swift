//
//  ChatExampleApp.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/10/24.
//

import SwiftUI
import BotStacks_ChatSDK

@main
struct ChatExampleApp: App {
    @ObservedObject var router = Router()
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router.navPath) {
                ContentView()
                    .ignoresSafeArea()
                    .navigationDestination(for: Router.Destination.self) { destination in
                        switch destination {
                        case .avatar: Avatars()
                        case .badges: Badges()
                        case .channelrow: ChannelRows()
                        case .channelgroup: ChannelGroups()
                        case .chatinput: ChatInputExample()
                        case .chatlist: ChatListExample()
                        case .chatmessage: ChatMessages()
                        case .chatmessagepreview: ChatMessagePreviews()
                        case .header: Headers()
                        case .spinner: Spinners()
                        case .userprofile: UserProfiles()
                        case .userselect: UserSelectExample()
                            
                        case .login: LoginView()
                        case .controller: ChatControllerExample()
                        }
                    }
            }.environmentObject(router)
        }
    }
}


class AppDelegate: UIResponder, UIApplicationDelegate {
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        BotStacksChat.shared.setupLogging(level: .debug, log: { log in print(log) })
        guard let apiKey = readPlist(list: "AppSecrets", key: "BOTSTACKS_API_KEY") else {
            return true
        }
        
        let googleMapsApiKey = readPlist(list: "AppSecrets", key: "GOOGLE_MAPS_API_KEY")
        print(googleMapsApiKey)
        
        BotStacksChat.shared.setup(apiKey: apiKey, googleMapsApiKey: googleMapsApiKey)
        
        return true
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
                                   
private func readPlist(list: String, key: String) -> String? {
    guard let plistPath = Bundle.main.path(forResource: list, ofType: "plist"),
          let plistData = FileManager.default.contents(atPath: plistPath) else {
        print("Error: Unable to locate or read plist file")
        return nil
    }

    do {
        // Deserialize plist data into a Swift object (Array, Dictionary, etc.)
        let plistObject = try PropertyListSerialization.propertyList(from: plistData, format: nil)
        
        // Handle the deserialized plist object
        if let plistDictionary = plistObject as? [String: Any] {
            // Access data from the plist dictionary
            if let value = plistDictionary[key] as? String {
                return value
            }
        } else {
            print("Error: Unable to parse plist data")
        }
    } catch {
        print("Error reading plist file: \(error)")
    }
    
    return nil
}
            
            
