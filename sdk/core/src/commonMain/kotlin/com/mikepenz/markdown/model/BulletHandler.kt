package com.mikepenz.markdown.model

import org.intellij.markdown.IElementType

/** An interface of providing use case specific un/ordered list handling.*/
internal fun interface BulletHandler {
    fun transform(type: IElementType, bullet: CharSequence?, index: Int): String
}
