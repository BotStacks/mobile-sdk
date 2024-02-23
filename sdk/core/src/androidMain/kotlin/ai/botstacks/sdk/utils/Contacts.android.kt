package ai.botstacks.sdk.utils

import ezvcard.Ezvcard
import ezvcard.VCard

actual typealias Vcard = VCard

actual fun parseVcard(data: String?): Vcard? {
    return runCatching { Ezvcard.parse(data).first() }.getOrNull()
}

actual fun Vcard.simpleName(): String {
    var name = formattedName?.value
    if (name.isNullOrBlank())
        name = structuredName?.let {
            (it.prefixes + listOf(
                it.given,
                it.family
            ) + it.suffixes).filter { !it.isNullOrBlank() }.joinToString(" ")
        }
    if (name.isNullOrBlank()) name = nickname?.values?.firstOrNull()
    return name.orEmpty()
}

actual fun Vcard.markdown(): String = "${
    simpleName()
}\n" +
        (telephoneNumbers?.joinToString("") {
            "[${
                it.types.firstOrNull()?.let { "${it.value}: " } ?: ""
            }${it.text}](${it.uri})\n"
        } ?: "") + (emails?.joinToString("") {
    "[${
        it.types.firstOrNull()?.let { "${it.value}: " } ?: ""
    }${it.value}](mailto:${it.value})\n"
} ?: "")