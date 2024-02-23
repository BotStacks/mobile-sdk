package ai.botstacks.sdk.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Contacts.CNContact
import platform.Contacts.CNContactFormatter
import platform.Contacts.CNContactFormatterStyle
import platform.Contacts.CNContactVCardSerialization
import platform.Contacts.CNLabeledValue
import platform.Contacts.CNPhoneNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.stringWithString

actual typealias Vcard = CNContact

@OptIn(ExperimentalForeignApi::class)
actual fun parseVcard(data: String?): Vcard? {
    data ?: return null

    @Suppress("CAST_NEVER_SUCCEEDS")
    val nsString = NSString.stringWithString(data) as NSString

    val nsData = nsString.dataUsingEncoding(NSUTF8StringEncoding) ?: return null

    return CNContactVCardSerialization.contactsWithData(nsData, null)?.first() as CNContact
}

actual fun Vcard.simpleName(): String {
    var name = CNContactFormatter.stringFromContact(
        this,
        CNContactFormatterStyle.CNContactFormatterStyleFullName
    )

    if (name.isNullOrBlank()) {
        val givenName = this.givenName
        val familyName = this.familyName
        val prefix = this.namePrefix
        val suffix = this.nameSuffix

        name = "$prefix $givenName $familyName $suffix"
    }
    return name
}

private fun Vcard.phoneNumbersRaw(): List<String> {
    return (this.phoneNumbers as List<CNLabeledValue>).mapNotNull { it.value as? CNPhoneNumber }
        .map { it.stringValue }
}

@Suppress("CAST_NEVER_SUCCEEDS")
private fun Vcard.emails(): List<String> {
    return (this.phoneNumbers as List<CNLabeledValue>).mapNotNull { it.value as? NSString }
        .mapNotNull { it as? String }
}


actual fun Vcard.markdown(): String = """
    ${simpleName()}
    ${phoneNumbersRaw().joinToString("") { "[${it}](TODO)\n" }}
    ${emails().joinToString("") { "[${it}](mailto:${it})\n" }}  
""".trimIndent()