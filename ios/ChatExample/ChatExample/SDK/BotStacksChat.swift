//
//  BotStacksChat.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/16/24.
//

import Foundation

import BotStacks_ChatSDK

class BotStacksChat {
    
    private let sharedInternal = BotStacksChatPlatform.companion.shared
    
    static let shared = BotStacksChat()
        
public func setup(
        apiKey: String, 
        giphyApiKey: String? = nil,
        googleMapsApiKey: String? = nil,
        delayLoad: Bool = false
    ) {
        sharedInternal.setup(apiKey: apiKey, giphyApiKey: giphyApiKey, googleMapsApiKey: googleMapsApiKey, delayLoad: delayLoad)
    }
    
    @MainActor
    public func login(
        userId: String,
        username: String,
        displayName: String? = nil,
        picture: String? = nil,
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        sharedInternal.login(userId: userId, username: username, displayName: displayName, picture: picture) { error in
            guard let error = error else {
                completion(.success(()))
                return
            }
            
            completion(.failure(error))
        }
    }
    
    public func logout() {
        BotStacksChatPlatform.companion.logout()
    }
    
    @MainActor
    public func load(
        completion: @escaping (Result<Void, Error>) -> Void
    ) {
        sharedInternal.load { error in
            guard let error = error else {
                completion(.success(()))
                return
            }
         
            completion(.failure(error))
        }
    }
    
    public func setupLogging(level: LogLevel, log: @escaping (String) -> Void = { msg in print(msg) }) {
        sharedInternal.setupLogging(level: level, log: log)
    }
    
    public func disableLogging() {
        sharedInternal.disableLogging()
    }
}
