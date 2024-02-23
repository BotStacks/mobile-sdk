package ai.botstacks.sdk.utils

object Platform
expect val Platform.isAndroid: Boolean
expect val Platform.usesCloseAffordanceOnSheets: Boolean

// on iOS, this would be true and on Android/Desktop, false depending on navigation system on device
expect val Platform.shouldUseSwipeBack: Boolean