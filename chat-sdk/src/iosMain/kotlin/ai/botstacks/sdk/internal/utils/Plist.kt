package ai.botstacks.sdk.internal.utils

import ai.botstacks.sdk.internal.Monitor
import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.create

internal fun <T> readPlist(name: String, key: String): T? {
    val bundle = NSBundle.mainBundle.pathForResource(name, "plist")
    if (bundle == null) {
        Monitor.error("Unable to read $name.plist")
        return null
    }

    val plist = NSDictionary.create(contentsOfFile = bundle)
    return readPlist(plist, key)
}

internal fun <T> readPlist(plist: NSDictionary?, key: String): T? {
    return plist?.objectForKey(key) as? T
}

internal fun hasKeysInPlist(name: String, vararg key: String): Boolean {
    val bundle = NSBundle.mainBundle.pathForResource(name, "plist")
    if (bundle == null) {
        Monitor.error("Unable to read $name.plist")
        return false
    }

    val plist = NSDictionary.create(contentsOfFile = bundle)
    return key.toList().all { readPlist<String>(plist, it) != null }
}