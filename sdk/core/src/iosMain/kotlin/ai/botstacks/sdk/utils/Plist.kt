package ai.botstacks.sdk.utils

import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.create

fun <T> readPlist(name: String, key: String): T? {
    val bundle = NSBundle.mainBundle.pathForResource(name, "plist")
    if (bundle == null) {
        println("Unable to read $name.plist")
        return null
    }

    val plist = NSDictionary.create(contentsOfFile = bundle)
    return readPlist(plist, key)
}

fun <T> readPlist(plist: NSDictionary?, key: String): T? {
    return plist?.objectForKey(key) as? T
}

fun hasKeysInPlist(name: String, vararg key: String): Boolean {
    val bundle = NSBundle.mainBundle.pathForResource(name, "plist")
    if (bundle == null) {
        println("Unable to read $name.plist")
        return false
    }

    val plist = NSDictionary.create(contentsOfFile = bundle)
    return key.toList().all { readPlist<String>(plist, it) != null }
}