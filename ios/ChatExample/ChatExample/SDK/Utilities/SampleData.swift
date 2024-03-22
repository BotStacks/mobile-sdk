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

func generateChannel(with users: [User] =  [], kind: ChatType = .group) -> Chat {
    let users = if users.isEmpty {
        (0..<20).map { _ in generateUser() }
    } else {
        users
    }
    
    let channel = Chat(id: UUID().uuidString, kind: kind)
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

func generateImageAttachment() -> MessageAttachment {
    let randomNumber = Int.random(in: 0...99)
    let image = "https://source.unsplash.com/random/?\(randomNumber)"
    let attachment = MessageAttachment(
        id: UUID().uuidString,
        type: AttachmentType.image,
        url: image,
        data: nil,
        mime: "image/jpeg",
        width: nil,
        height: nil,
        duration: nil,
        address: nil,
        latitude: nil,
        longitude: nil
    )
    
    return attachment
}

func generateLocationAttachment() -> MessageAttachment {
    let location = Location(
        latitude: 42.3314,
        longitude: 83.0458,
        address: nil
    )
    
    let attachment = MessageAttachment(
        id: UUID().uuidString,
        type: AttachmentType.location,
        url: "",
        data: nil,
        mime: nil,
        width: nil,
        height: nil,
        duration: nil,
        address: nil,
        latitude: location.latitude,
        longitude: location.longitude
    )
    
    return attachment
}

func generateMessage(
    from user: User,
    in chat: Chat,
    text: String = faker.lorem.sentences(amount: 2),
    attachments: [MessageAttachment] = []
) -> Message {
    let message = Message(
        id: UUID().uuidString,
        createdAt: NSDateKt.toInstant(faker.date.backward(days: 3)),
        userID: user.id,
        parentID: nil,
        chatID: chat.id,
        _attachments: attachments,
        _reactions: []
    )

    message.updateText(text: text)
    
    chat.addMessage(message: message)
    
    return message
}

func generateChatWithMessages() -> Chat {
    let user1 = generateUser()
    let user2 = generateUser()
    let user3 = generateUser()
    let user4 = generateUser()
    let chat = generateChannel(with: [user1, user2, user3, user4], kind: .group)
    let messages = [
        generateMessage(from: user1, in: chat),
        generateMessage(from: user1, in: chat),
        generateMessage(from: user2, in: chat),
        generateMessage(from: user3, in: chat, attachments: [generateImageAttachment()]),
        generateMessage(from: user4, in: chat, attachments: [generateLocationAttachment()])
    ]
    
    messages.forEach { message in
        chat.addMessage(message: message)
    }
    
    return chat
}

func generateChatList() -> [Chat] {
    let chats = (0..<5).map { _ in generateChatWithMessages() }
    
    return chats
}

