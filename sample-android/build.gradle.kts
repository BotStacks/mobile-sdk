plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.github.triplet.play)
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":sdk:core"))
                
                implementation(compose.runtime)
                implementation(compose.runtimeSaveable)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.animation)
                implementation(compose.preview)
                implementation(compose.uiTooling)

                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.androidx.lifecycle.runtime)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.androidx.work.runtime)

                implementation(firebaseLibs.firebaseAnalyticsKtx)
                implementation(firebaseLibs.firebaseMessagingKtx)

                implementation(libs.giphy)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.junit)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.compose.ui)
                implementation(libs.androidx.test.espresso)
                implementation(libs.androidx.test.junit)
            }
        }
    }
}

android {
    namespace = "ai.botstacks.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        versionCode = 6
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keyFile = file("../playstore")
            if (keyFile.exists()) {
                val storePass = project.properties["store.password"] as? String ?: ""
                val alias = project.properties["key.alias"] as? String ?: ""
                val keyPass = project.properties["key.password"] as? String ?: ""
                if (storePass.isNotEmpty() && alias.isNotEmpty() && keyPass.isNotEmpty()) {
                    storeFile = keyFile
                    storePassword = storePass
                    keyAlias = alias
                    keyPassword = keyPass
                }
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),("proguard-rules.pro"))
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += listOf("META-INF/INDEX.LIST","META-INF/io.netty.versions.properties")
        }
    }
}


tasks {
    afterEvaluate {
        android.applicationVariants.forEach { variant ->
            register("open${variant.name.capitalize()}") {
                notCompatibleWithConfigurationCache("not supported")
                dependsOn("install${variant.name.capitalize()}")

                doLast {
                    exec {
                        commandLine( "adb shell monkey -p ${variant.applicationId} -c android.intent.category.LAUNCHER 1".split(" "))
                    }
                }
            }
        }
    }
}

play {
    serviceAccountCredentials.set(file("../gpp.json"))
}