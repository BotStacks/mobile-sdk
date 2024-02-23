package ai.botstacks.sdk.utils

expect class Vcard

expect fun parseVcard(data: String?): Vcard?

expect fun Vcard.simpleName(): String

expect fun Vcard.markdown(): String