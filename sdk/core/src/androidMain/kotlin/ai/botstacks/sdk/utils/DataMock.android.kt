package ai.botstacks.sdk.utils

import net.datafaker.Faker

actual typealias DataMock = Faker

actual fun DataMock.companyName(): String = company().name()
actual fun DataMock.emoji(): String = emoji().toString()
actual fun DataMock.funnyName(): String = funnyName().name()
actual fun DataMock.loremParagraph(): String = lorem().paragraph()
actual fun DataMock.loremSentence(): String = lorem().sentence()
actual fun DataMock.name(): String = name().name()
actual fun DataMock.smileyEmoji(): String = emoji().smiley()
actual fun DataMock.username(): String = name().username()