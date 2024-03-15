//
//  SampleData.swift
//  ChatExample
//
//  Created by Brandon McAnsh on 3/15/24.
//

import Foundation
import BotStacks_ChatSDK
import Fakery

let faker = Faker()

func generateUser() -> User {
    let user = User(id: UUID().uuidString)
    user.username = faker.internet.username()
    user.displayName = "\(faker.name.firstName()) \(faker.name.lastName())"
    
    let gender = faker.gender.binaryType()
    let randomNumber = Int.random(in: 0...99)
    switch gender {
    case "Male":
        user.avatar = "https://randomuser.me/api/portraits/men/\(randomNumber).jpg"
    case "Female":
        user.avatar = "https://randomuser.me/api/portraits/women/\(randomNumber).jpg"
    default: break
    }
    
    return user
}

func generateChannel() -> Chat {
    let users = (0..<20).map { _ in generateUser() }
    
    let channel = Chat(id: UUID().uuidString, kind: ChatType.group)
    channel.members.addObjects(from: users.map({ user in
        Participant(
            user_id: user.id,
            chat_id: channel.id,
            created_at: NSDateKt.toInstant(Date()),
            role: MemberRole.member
        )
    }))
    
    channel.name = faker.lorem.sentence(wordsAmount: 4)
    let randomNumber = Int.random(in: 0...99)
    channel.image = "https://source.unsplash.com/random/?\(randomNumber)"
    
    return channel
}

func generateChannelList() -> [Chat] {
    let channels = (0..<5).map { _ in generateChannel() }
    
    return channels
}
