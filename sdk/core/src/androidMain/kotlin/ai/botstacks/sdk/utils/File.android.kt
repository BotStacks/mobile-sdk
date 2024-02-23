package ai.botstacks.sdk.utils


import android.webkit.MimeTypeMap
import com.mohamedrejeb.calf.io.KmpFile
import com.mohamedrejeb.calf.io.readByteArray

actual fun KmpFile.contentType(): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(this.extension)
}

actual fun KmpFile.size(): Long {
    return this.length()
}

actual fun KmpFile.readBytes(): ByteArray = readByteArray()