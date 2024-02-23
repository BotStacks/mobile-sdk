package com.mohamedrejeb.calf.io

import ai.botstacks.sdk.utils.readData
import ai.botstacks.sdk.utils.uuid
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.pathExtension
import platform.Foundation.writeToURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

interface TemporaryURL {
    val contentURL: NSURL
}

class TemporaryFileURL(
    private val content: NSURL,
    extension: String = content.pathExtension().orEmpty()
) : TemporaryURL {
    override val contentURL: NSURL =
        NSURL.fileURLWithPath("${NSTemporaryDirectory()}/${uuid()}.$extension").apply {
            content.dataRepresentation.writeToURL(this, true)
        }
}

class TemporaryImageURL(
    private val content: UIImage,
) : TemporaryURL {

    constructor(contentURL: NSURL): this(UIImage(contentURL.readData()))
    override val contentURL: NSURL
        get() {
            val jpgData = UIImageJPEGRepresentation(content, 1.0)
            val path = "${NSTemporaryDirectory()}/${uuid()}.jpg"
            NSFileManager.defaultManager().createFileAtPath(path, jpgData, null)
            return NSURL.fileURLWithPath(path)
        }
}