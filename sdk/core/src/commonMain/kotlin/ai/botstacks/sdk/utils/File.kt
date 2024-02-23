package ai.botstacks.sdk.utils

import com.mohamedrejeb.calf.io.KmpFile

expect fun KmpFile.contentType(): String?
expect fun KmpFile.size(): Long

expect fun KmpFile.readBytes(): ByteArray
