package ai.botstacks.sdk.utils

actual val Platform.isAndroid: Boolean
    get() = true

actual val Platform.usesCloseAffordanceOnSheets: Boolean
    get() = false

actual val Platform.shouldUseSwipeBack: Boolean
    get() = false
