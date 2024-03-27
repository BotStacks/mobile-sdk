package ai.botstacks.sdk

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

fun Project.addAllMultiplatformTargets() {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            applyBotStacksHierarchyTemplate()

            val isAndroidApp = plugins.hasPlugin("com.android.application")
            val isAndroidLibrary = plugins.hasPlugin("com.android.library")
            if (isAndroidApp || isAndroidLibrary) {
                androidTarget {
                    if (isAndroidLibrary) {
                        publishLibraryVariants("release")
                    }
                }
            }

            val xcf = XCFramework()
            val iosTargets = listOf(iosX64(), iosArm64(), iosSimulatorArm64())
            iosTargets.forEach {
                it.binaries.framework {
                    baseName = "BotStacks_ChatSDK"
                    xcf.add(this)
                }
            }
        }
    }
}