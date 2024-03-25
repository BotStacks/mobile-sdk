//
//  Router.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

final class Router: ObservableObject {
    
    public enum Destination: Codable, Hashable {
        
        case showcase
        
        // MARK: components
        case avatar
        case badges
        case channelrow
        case channelgroup
        case chatinput
        case chatlist
        case chatmessage
        case chatmessagepreview
        case header
        case messagelist
        case spinner
        case userprofile
        case userselect
        
        // MARK: views
        case channeluserselect(BSCSDKChannelUserSelectionState)
        case channelsettingsexample
        case channelsettings(String)
        case channelsettings_userselect(BSCSDKChannelSettingsState)
        
        // MARK: E2E Controller nodes
        case controller
    
    }
    
    @Published var navPath = NavigationPath()
    
    func navigate(to destination: Destination) {
        navPath.append(destination)
    }

    func navigateBack() {
        navPath.removeLast()
    }
    
    func navigateToRoot() {
        navPath.removeLast(navPath.count)
    }
}
