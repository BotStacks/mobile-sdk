/*
 * Copyright (c) 2023.
 */

package io.inappchat.sdk.utils

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.inappchat.sdk.models.*
import io.inappchat.sdk.state.*
import net.datafaker.Faker
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

val faker = Faker()

fun genCurrentUser() = User.current ?: genU().let {
  User.current = it
  it
}

fun genU(): User {
  val u = User(uuid())
  u.email = faker.internet().emailAddress()
  u.username = faker.name().username()
  u.avatar = ift(Random.nextBoolean(), randomImage(), null)
  u.status = AvailabilityStatus.values().random()
  u.statusMessage = ift(Random.nextBoolean(), faker.lorem().sentence(), null)
  u.lastSeen = ift(
    Random.nextBoolean(),
    Instant.now().minusSeconds(Random.nextLong(10000000)),
    null
  )
  u.displayName = faker.funnyName().name()
  return u
}

fun reqU() {
  if (Chats.current.cache.users.isEmpty()) {
    (0..100).map { genU() }
  }
}

fun reqT() {
  if (Chats.current.cache.threads.isEmpty()) {
    (0..100).map { genT() }
  }
}

fun randomUsers() = randomAmount(Chats.current.cache.users.values).let {
  ift(
    it.isEmpty(),
    (0..30).map { genU() },
    it
  )
}

fun randomUser() = Chats.current.cache.users.values.randomOrNull() ?: genU()

fun genG(): Group {
  val g = Group(uuid())
  g.name = faker.company().name()
  g.description = ift(chance(4, 5), faker.lorem().paragraph(), null)
  g.avatar = ift(Random.nextBoolean(), randomImage(), null)
  g._private = Random.nextBoolean()
  val members = randomUsers().let { ift(it.isEmpty(), random(40, ::genU), it) }
  g.participants.addAll(members.map {
    Participant(
      it.email,
      it.id,
      ift(Random.nextBoolean(), Participant.Role.admin, Participant.Role.user),
      LocalDateTime.now().minusSeconds(Random.nextLong(1000000))
        .atOffset(ZoneOffset.UTC)
    )
  })
  if (Random.nextBoolean())
    g.invites.addAll(randomAmount(members))
  return g
}

fun genA(): Attachment {
  val kind = AttachmentKind.values().random()
  var url = when (kind) {
    AttachmentKind.video ->
      "https://download.samplelib.com/mp4/sample-5s.mp4"
    AttachmentKind.audio ->
      "https://file-examples.com/storage/fe0358100863d05afed02d2/2017/11/file_example_MP3_5MG.mp3"
    AttachmentKind.image, AttachmentKind.file -> randomImage()
  }
  return Attachment(url, kind)
}

fun bool() = Random.nextBoolean()

fun genL() = ift(
  bool(),
  Location(address = faker.address().fullAddress()),
  Location(
    latitude = Random.nextInt(-90, 90).toBigDecimal() * Random.nextFloat()
      .toBigDecimal(),
    longitude = Random.nextInt(-180, 180).toBigDecimal() * Random.nextFloat()
      .toBigDecimal()
  )
)

fun genC() = Contact(
  name = faker.funnyName().name(),
  numbers =
  random(
    Random.nextInt(1, 3),
    { PhoneNumber(faker.phoneNumber().phoneNumber(), listOf("work", "home").random()) }),
  emails =
  random(
    Random.nextInt(1, 3),
    { Email(faker.internet().emailAddress(), listOf("work", "home").random()) })
)

fun genM(
  thread: String = genT().id,
  parent: String? = null,
  attachment: Attachment? = null
): Message {
  val m = Message(
    uuid(), Instant.now().minusSeconds(Random.nextLong(100000L)),
    randomUser().id,
    parent,
    thread,
    attachment ?: ift(bool(), genA(), null),
    location = ift(bool(), genL(), null),
    contact = ift(bool(), genC(), null)
  )
  m.text = faker.lorem().word()
  if (parent == null && chance(1, 5)) {
    m.replies.items.addAll((0 until Random.nextInt(10)).map { genM(m.threadID, m.id) })
  }
  if (chance(1, 4))
    m.reactions.addAll(
      random(
        10,
        { Reaction(faker.slackEmoji().emoji(), Random.nextInt(1, 10), listOf()) })
    )
  m.replyCount = m.replies.items.size
  m.favorite = chance(1, 5)
  m.sending = Random.nextBoolean()
  m.room?.addMessage(m)
  m.room?.items?.sortByDescending { it.createdAt }
  m.room?.latest = m.room?.items?.first()
  return m
}

fun genImageMessage() = genM(attachment = Attachment(randomImage(), AttachmentKind.image))
fun genFileMessage() = genM(attachment = Attachment(randomImage(), AttachmentKind.file))
fun genContactMessage() = Message(
  uuid(), Instant.now().minusSeconds(Random.nextLong(100000L)),
  randomUser().id,
  null,
  uuid(),
  contact = genC()
)

fun genTextMessage(user: User = randomUser()) = Message(
  uuid(), Instant.now().minusSeconds(Random.nextLong(100000L)),
  user.id,
  null,
  uuid(),
).apply { updateText(faker.lorem().paragraph()) }

fun genLocationMessage() = Message(
  uuid(), Instant.now().minusSeconds(Random.nextLong(100000L)),
  randomUser().id,
  null,
  uuid(),
  location = genL()
)

fun genT(): Room {
  val u = ift(bool(), genU(), null)
  val g = u?.let { null } ?: genG()
  val r = Room(
    uuid(),
    u,
    g
  )
  return r
}

class SampleUser : PreviewParameterProvider<User> {
  override val values: Sequence<User> = (0..40).map {
    genU()
  }.asSequence()
}

class SampleGroup : PreviewParameterProvider<Group> {
  override val values: Sequence<Group> = (0..3).map {
    genG()
  }.asSequence()
}

class SampleMessage : PreviewParameterProvider<Message> {
  override val values: Sequence<Message> = sequenceOf(genM())
}

class SampleFn : PreviewParameterProvider<Fn> {
  override val values: Sequence<Fn> = sequenceOf({})

}

class SampleRoom : PreviewParameterProvider<Room> {
  override val values: Sequence<Room> = (0..40).map { genT() }.asSequence()
}