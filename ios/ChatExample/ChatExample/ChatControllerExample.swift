//
//  ChatControllerExample.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/16/24.
//

import Foundation
import SwiftUI

struct ChatControllerExample: View {
    
    @EnvironmentObject var router: Router
    
    var body: some View {
        BotStacksChatController { router.navigateToRoot() }
            .navigationBarTitle(Text(""), displayMode: .inline) // Hide navigation bar title
            .navigationBarBackButtonHidden()
    }
}
