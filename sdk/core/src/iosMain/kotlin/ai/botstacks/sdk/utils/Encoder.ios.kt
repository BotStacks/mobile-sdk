package ai.botstacks.sdk.utils

import platform.Foundation.NSCharacterSet
import platform.Foundation.NSMutableCharacterSet
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters

actual fun urlEncode(value: String, encoding: String) : String {
    val set = NSCharacterSet.letterCharacterSet().mutableCopy() as NSMutableCharacterSet
    set.addCharactersInString(".")

    return NSString.create(string = value).stringByAddingPercentEncodingWithAllowedCharacters(
        allowedCharacters = set
    ).orEmpty()
}