/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.utils


fun String.replace(pattern: Regex, block: (String) -> String): String {
    // TODO:
//    var _text = this
//    val matcher = phoneRegex.matchEntire(this)
//    var match = matcher?.next() != null
//    var groupIndex = 0
//    while (match) {
//        val num = matcher?.groups?.get(groupIndex)
//        _text = _text.replace(num, block(num))
//        match = matcher.find()
//    }
    return this
}


val phoneRegex = Regex("(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}")
fun linkPhones(text: String) = text.replace(phoneRegex) { "[$it](tel:$it)" }

val linkRegex =
        Regex("((http|ftp|https):\\//)?([\\w_-]+(?:\\.[\\w_-]+)+)([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])")

fun linkLinks(text: String) = text.replace(linkRegex) { "[$it](it)" }

val mentionRegex = Regex("(@[0-9a-zA-Z]+)")
fun linkMentions(text: String) =
        text.replace(mentionRegex) { "[$it](${bundleUrl()}://botstackschat/user/$it)" }

