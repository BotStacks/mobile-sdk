package ai.botstacks.sdk.ui

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

fun UIApplication.Companion.rootViewController(): UIViewController? {
    return ((this.sharedApplication.connectedScenes.first() as? UIWindowScene)?.windows?.first() as? UIWindow)?.rootViewController()
}