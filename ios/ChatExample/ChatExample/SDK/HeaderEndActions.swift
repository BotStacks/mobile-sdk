//
//  HeaderEndActions.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/14/24.
//

import Foundation
import SwiftUI
import BotStacks_ChatSDK

///
/// End actions to be used in a `Header` via `Header#withEndAction`
///
public enum EndAction {
    /// Renders an End Action with a visible 'Next' label
    ///
    /// - Parameters:
    ///  - onClick: Callback for when the action is clicked.
    ///
    case next(onClick: () -> Void)
    /// Renders an End Action with a visible 'Save' label
    ///
    /// - Parameters:
    ///  - onClick: Callback for when the action is clicked.
    ///
    case save(onClick: () -> Void)
    /// Renders an End Action with a visible 'Menu' label
    ///
    /// - Parameters:
    ///  - onClick: Callback for when the action is clicked.
    ///
    case menu(onClick: () -> Void)
    /// Renders an End Action with a visible 'Create' label
    ///
    /// - Parameters:
    ///  - onClick: Callback for when the action is clicked.
    ///
    case create(onClick: () -> Void)
}
