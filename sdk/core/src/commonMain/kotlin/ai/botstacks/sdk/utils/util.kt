/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.state.Location
import ai.botstacks.sdk.type.AttachmentInput
import ai.botstacks.sdk.type.AttachmentType
import com.apollographql.apollo3.api.Optional
import com.benasher44.uuid.uuid4

fun bundleUrl() = BotStacksChat.shared.appIdentifier
fun uuid() = uuid4().toString()

typealias Fn = () -> Unit

fun <A> ift(cond: Boolean, a: A, b: A) = if (cond) a else b
fun <A> ifteq(a: A, b: A) = if (a == b) a else b

@Composable
fun String.annotated() = AnnotatedString(this)


fun contactUriToVCard(url: String): Vcard {
    throw NotImplementedError()
//    Log.v("IAC", "Contact URI $uri")
//    val result = Contacts(BotStacksChat.shared.appContext).query().where {
//        Contact.Id.equalToIgnoreCase(uri.lastPathSegment!!)
//    }.include {
//        listOf(
//                Phone.Number,
//                Phone.NormalizedNumber,
//                Phone.Type,
//                Email.Type,
//                Email.Address,
//                Name.DisplayName,
//                Name.FamilyName,
//                Name.GivenName,
//        )
//    }.find()
//    return result.map {
//        VCard().apply {
//            it.displayNamePrimary?.let {
//                addFormattedName(FormattedName(it))
//            }
//            it.displayNameAlt?.let {
//                addFormattedNameAlt(FormattedName(it))
//            }
//            it.nameList().forEach {
//                structuredName = StructuredName().apply {
//                    given = it.givenName
//                    family = it.familyName
//                    it.prefix?.let { prefixes.add(it) }
//                    it.suffix?.let { suffixes.add(it) }
//                }
//            }
//            it.phones().toList().forEach {
//                addTelephoneNumber(Telephone(it.normalizedNumber ?: it.number
//                ?: it.primaryValue).apply {
//                    it.type?.let {
//                        this.addParameter("type", it.name)
//                    }
//                })
//            }
//            it.emails().toList().map {
//                addEmail(Email(it.address ?: it.primaryValue).apply {
//                    it.type?.let {
//                        this.addParameter("type", it.name)
//                    }
//                })
//            }
//        }
//    }.first()
}

fun Vcard.attachment(): AttachmentInput {
    // TODO:
    throw NotImplementedError()
//    return AttachmentInput(
//            url = "data",
//            type = AttachmentType.vcard,
//            data = Optional.present(Ezvcard.write(this).go()),
//            id = uuid()
//    )
}


fun Location.attachment(): AttachmentInput {
    return AttachmentInput(
        longitude = Optional.present(this.longitude),
        latitude = Optional.present(this.latitude),
        id = uuid(),
        type = AttachmentType.location,
        url = "data"
    )
}

fun String.imageAttachment() = AttachmentInput(
    url = this.toString(),
    type = AttachmentType.image,
    id = uuid()
)