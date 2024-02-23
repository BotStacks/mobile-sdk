package ai.botstacks.sdk.utils

actual val Platform.isAndroid: Boolean
    get() = false

actual val Platform.usesCloseAffordanceOnSheets: Boolean
    get() = true

actual val Platform.shouldUseSwipeBack: Boolean
    get() = true
