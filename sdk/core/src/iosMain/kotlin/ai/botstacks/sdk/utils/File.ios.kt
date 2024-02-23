package ai.botstacks.sdk.utils

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.NSURLFileSizeKey
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.pathExtension
import platform.UniformTypeIdentifiers.UTType
import platform.posix.memcpy

actual fun KmpFile.contentType(): String? {
    val pathExtension = this.pathExtension().orEmpty()
    return UTType.typeWithFilenameExtension(pathExtension)?.preferredMIMEType
}

@OptIn(ExperimentalForeignApi::class)
actual fun KmpFile.size(): Long {
    return runCatching {
        val resources = resourceValuesForKeys(listOf(NSURLFileSizeKey), null)
        resources?.get(NSURLFileSizeKey) as? Long
    }.getOrNull() ?: -1L
}

fun NSURL.readData(): NSData {
    var result: NSData? = null
    while (result == null) {
        val data = NSData.dataWithContentsOfURL(this)
        if (data != null)
            result = data
    }

    return result
}

actual fun KmpFile.readBytes(): ByteArray =
    with(readData()) {
        ByteArray(length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), bytes, length)
            }
        }
    }