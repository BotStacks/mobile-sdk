//
//  Views.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/23/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

internal struct ChannelUserSelect : View {
    
    @ObservedObject var state: BSCSDKChannelUserSelectionState
    
    var body: some View {
        ComponentView(title: "Select Users", canScroll: false) {
            SelectChannelUsersView(state: state)
                .frame(
                    maxWidth: .infinity,
                    maxHeight: .infinity,
                    alignment: .topLeading
                )
        }
    }
}
