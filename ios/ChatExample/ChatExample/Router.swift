//
//  Router.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import SwiftUI

final class Router: ObservableObject {
    
    public enum Destination: Codable, Hashable {
        // MARK: components
        case avatars
        case badges
        case channelrow
        case channelgroup
        case chatinput
        case headers
        case spinners
        case userprofiles
        
        // MARK: E2E Controller nodes
        case login
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
