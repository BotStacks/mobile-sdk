/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import ai.botstacks.sdk.fragment.FMessage
import ai.botstacks.sdk.state.*
import ai.botstacks.sdk.type.AttachmentType
import ai.botstacks.sdk.type.ChatType
import ai.botstacks.sdk.type.MemberRole
import ai.botstacks.sdk.type.OnlineStatus
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val faker = DataMock()

fun genCurrentUser() = User.current ?: genU().let {
    User.current = it
    it
}

fun genU(): User {
    val u = User(uuid())
    u.username = faker.username()
    u.avatar = randomImage()
    u.status = OnlineStatus.entries.toTypedArray().random()
    u.statusMessage = ift(Random.nextBoolean(), faker.loremSentence(), null)
    u.lastSeen = ift(
        Random.nextBoolean(),
        Clock.System.now()
            .minus(Random.nextLong(10000000).toDuration(DurationUnit.SECONDS)),
        null
    )
    u.displayName = faker.funnyName().toString()

    return u
}

fun genMember(chatId: String = uuid()): Member {
    val user = genU()
    BotStacksChatStore.current.cache.users[user.id] = user
    return Member(user.id, chatId, Clock.System.now(), MemberRole.Member)
}

fun reqU() {
    if (BotStacksChatStore.current.cache.users.isEmpty()) {
        (0..100).map { genU() }
    }
}

fun reqT() {
    if (BotStacksChatStore.current.cache.chats.isEmpty()) {
        (0..100).map { genChat() }
    }
}

fun randomUsers() = randomAmount(BotStacksChatStore.current.cache.users.values).let {
    ift(
        it.isEmpty(),
        (0..30).map { genU() },
        it
    )
}

fun randomUser() = BotStacksChatStore.current.cache.users.values.randomOrNull() ?: genU()

fun genG(): Chat {
    val g = Chat(uuid(), ChatType.Group)
    g.name = faker.companyName()
    g.description = ift(chance(4, 5), faker.loremParagraph(), null)
    g.image = ift(Random.nextBoolean(), randomImage(), null)
    g._private = Random.nextBoolean()
    val members = randomUsers().let { ift(it.isEmpty(), random(40, ::genU), it) }
        .map {
            Member(it.id, g.id, Clock.System.now(), MemberRole.entries.toTypedArray().random())
        }
    if (Random.nextBoolean())
        g.invites.addAll(randomAmount(members).map { it.user })
    return g
}

fun genA(
    kind: AttachmentType = listOf(
        AttachmentType.video,
        AttachmentType.audio,
        AttachmentType.image,
        AttachmentType.file
    ).random()
): FMessage.Attachment {
    val url = when (kind) {
        AttachmentType.video ->
            "https://download.samplelib.com/mp4/sample-5s.mp4"

        AttachmentType.audio ->
            "https://file-examples.com/storage/fe0358100863d05afed02d2/2017/11/file_example_MP3_5MG.mp3"

        else -> randomImage()
    }
    return FMessage.Attachment(uuid(), kind, url, null, null, null, null, null, null, null, null)
}

fun bool() = Random.nextBoolean()

fun genM(
    chat: String = genChat().id,
    parent: String? = null,
    attachments: SnapshotStateList<FMessage.Attachment>? = null,
    user: User = randomUser()
): Message {
    val _attachments =
        attachments ?: ift(bool(), mutableStateListOf(genA()), null)
    val m = Message(
        uuid(), Clock.System.now().minus(Random.nextLong(100000L).seconds),
        user.id,
        parent,
        chat,
        _attachments ?: mutableStateListOf()
    )
    m.updateText(faker.loremParagraph())
    if (parent == null && chance(1, 5)) {
        m.replies.items.addAll((0 until Random.nextInt(10)).map { genM(m.chatID, m.id) })
    }
    if (chance(1, 4))
        m.reactions.addAll(
            random(
                10
            ) {
                faker.smileyEmoji() to (Chat.get(chat)?.let { randomAmount(it.members) }
                    ?.map { it.user_id }?.toMutableStateList() ?: mutableStateListOf())
            }
        )
    m.replyCount = m.replies.items.size
    m.favorite = chance(1, 5)
    m.chat.addMessage(m)
    m.chat.items.sortByDescending { it.createdAt }
    m.chat.latest = m.chat.items.first()
    return m
}

fun genChat() = if (bool()) genG() else genDM()

fun genImageMessage(user: User = genU()) =
    genM(attachments = mutableStateListOf(genA(AttachmentType.image)), user = user)

fun genFileMessage() = genM(attachments = mutableStateListOf(genA(AttachmentType.file)))

fun genChatextMessage(user: User = randomUser(), chat: String = genG().id) = Message(
    uuid(), Clock.System.now().minus(Random.nextLong(100000L).seconds),
    user.id,
    null,
    chat,
).apply {
    updateText(faker.loremParagraph())
    reactions.addAll(
        random(
            4
        ) {
            (faker.smileyEmoji() to randomAmount(randomUsers()).map { it.id }
                .toMutableStateList())
        }
    )
    currentReaction = reactions.firstOrNull()?.first
    replyCount = if (Random.nextBoolean()) Random.nextInt(20) else 0
}

fun genRepliesMessage() = genChatextMessage(chat = genG().id).apply {
    replies.items.addAll(random(4) { genChatextMessage(chat = this.chatID) })
    replyCount = replies.items.size
}

fun genCommonChannels(user: User) = listOf(
    Chat(uuid4().toString(), ChatType.Group).apply {
        name = "Hack Hub"
        image = randomImage()
        members.apply {
            addAll(
                listOf(genMember(id), genMember(id), genMember(id), genMember(id)),
            )
        }
    },
    Chat(uuid4().toString(), ChatType.Group).apply {
        name = "Dev Artistry"
        image = randomImage()
        members.apply {
            addAll(
                listOf(genMember(id), genMember(id), genMember(id), genMember(id)),
            )
        }
    },
    Chat(uuid4().toString(), ChatType.Group).apply {
        name = "Craft Studios"
        image = randomImage()
        members.apply {
            addAll(
                listOf(genMember(id), genMember(id), genMember(id), genMember(id)),
            )
        }
    },
    Chat(uuid4().toString(), ChatType.Group).apply {
        name = "Canvas Design"
        image = randomImage()
        members.apply {
            addAll(
                listOf(genMember(id), genMember(id), genMember(id), genMember(id)),
            )
        }
    }
)

fun genDM() =
    Chat(uuid(), ChatType.DirectMessage).apply {
        val u = genU()
        val c = genCurrentUser()
        members.add(Member(u.id, id, Clock.System.now(), MemberRole.Member))
        members.add(Member(c.id, id, Clock.System.now(), MemberRole.Member))
        items.addAll(
            random(
                10,
                { genChatextMessage(chat = id, user = u) })
        )
        items.sortByDescending { it.createdAt }
        latest = items.firstOrNull()
        if (!items.isEmpty())
            unreadCount = Random.nextInt(0, items.size)
    }

//class SampleUser : PreviewParameterProvider<User> {
//    override val values: Sequence<User> = (0..40).map {
//        genU()
//    }.asSequence()
//}
//
//class SampleChat : PreviewParameterProvider<Chat> {
//    override val values: Sequence<Chat> = (0..3).map {
//        genG()
//    }.asSequence()
//}
//
//class SampleMessage : PreviewParameterProvider<Message> {
//    override val values: Sequence<Message> = sequenceOf(genM())
//}
//
//class SampleFn : PreviewParameterProvider<Fn> {
//    override val values: Sequence<Fn> = sequenceOf({})
//
//}
