//
//  Login.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/16/24.
//

import Foundation
import BotStacks_ChatSDK
import SwiftUI

struct LoginView: View {
    
    @EnvironmentObject var router: Router
    
    @State private var loggingIn: Bool = false
    
    var body: some View {
        VStack {
            HStack {
                Button("Login") {
                    withAnimation {
                        loggingIn = true
                    }
                  
                    BotStacksChat.shared.login(userId: "testuser-ios", username: "testuser-ios", displayName: nil, picture: nil) { result in
                        withAnimation {
                            loggingIn = false
                        }
                        
                        switch result {
                        case .success:
                            router.navigate(to: .showcase)
                        case .failure(let error):
                            print(error)
                        }
                    }
                }
                if loggingIn {
                    ProgressView()
                        .opacity(1)
                        .transition(.opacity)
                } else {
                    ProgressView()
                        .opacity(0)
                }
            }
        }
    }
}
