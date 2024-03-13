# Setup

## Environment Setup

Our Chat SDK uses [Moko Resources](https://github.com/icerockdev/moko-resources) to include the internal assets in the SDK for iOS.

Since iOS doesn't bundle resources for static frameworks, we have to add the Moko resource plugin and setup the environment properly for inclusion.

### Step 1: Update Gradle

```kotlin
build.gradle.kts

buildscript {
    dependencies {
        // required for now to include resources from Chat SDK
+       classpath(libs.moko.resources.generator)
    }
}
```

```kotlin
shared/build.gradle.kts

plugins {
    // required for now to include resources from Chat SDK
+   id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    [...]
    sourceSets {
        [...]
+       val iosX64Main by getting
+       val iosArm64Main by getting
+       val iosSimulatorArm64Main by getting
            
        iosMain {
+           dependsOn(commonMain.get())
+           iosX64Main.dependsOn(this)
+           iosArm64Main.dependsOn(this)
+           iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                // required for now to include resources from Chat SDK
+               implementation(libs.moko.resources)
            }
        }
    }
}
```

### Step 2: Add Build Phase to XCode

Per the documentation from Moko [here](https://github.com/icerockdev/moko-resources?tab=readme-ov-file#with-orgjetbrainskotlinnativecocoapods) we need to add a Run Script Build Phase, with the following script:

```shell
cd "$SRCROOT/.."

./gradlew :shared:copyFrameworkResourcesToApp \
    -Pmoko.resources.BUILT_PRODUCTS_DIR="$BUILT_PRODUCTS_DIR" \
    -Pmoko.resources.CONTENTS_FOLDER_PATH="$CONTENTS_FOLDER_PATH" \
    -Pkotlin.native.cocoapods.platform="$PLATFORM_NAME" \
    -Pkotlin.native.cocoapods.archs="$ARCHS" \
    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
```

Be sure to update `shared` with the name of your shared module. The Multiplatform Wizard usually uses `shared` or `composeApp`.

### Step 3: Setup Cocoapods

Add a podfile with GoogleMaps and Giphy:

```
target 'iosApp' do
#   use_frameworks!
  platform :ios, '15.0'

+ pod 'GoogleMaps', '8.4.0'
+ pod 'Giphy', '2.2.8'
end
```

Setup cocoapods in gradle:

```kotlin
plugins {
+   kotlin("native.cocoapods")
}
```

```kotlin
kotlin {
    [...]
    cocoapods {
        name = "shared"
        version = "1.0"
        homepage = "https://botstacks.ai"
        summary = "Some cool story"
        ios.deploymentTarget = "15.0"

        podfile = file("../iosApp/Podfile")

        framework {
            baseName = "shared"
            isStatic = true
        }

        pod("Giphy") {
            moduleName = "GiphyUISDK"
            version = "2.2.8"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("GoogleMaps") {
            version = "8.4.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
}
```

## Quick Start

In order to display any of the UI components and access chat data, you must first initialize the SDK and log in as a chat user.

### Step 1: Initialize the SDK

In each platform (android/ios), call BotStacksChat.shared.setup with your API key. You can obtain your API key from the [Botstacks Dashboard](https://dashboard.botstacks.ai/settings). If you don’t yet have one, you can [create one for FREE](https://dashboard.botstacks.ai/)!

#### Android
```kotlin
main.android.kt

@Composable
fun MainView() {
    BotStacksChat.shared.setup(
        context = LocalContext.current,
        apiKey = stringResource(R.string.botstacks_api_key),
    )

    App()
}
```

#### iOS
```kotlin
main.ios.kt

fun MainViewController() = ComposeUIViewController(
    configure = {
        onFocusBehavior = OnFocusBehavior.DoNothing
    }
) {
    val apiKey = readPlist<String>("AppSecrets", "BOTSTACKS_API_KEY") ?: throw IllegalArgumentException("BotStacks API Key not provided")

    BotStacksChat.shared.setup(
        apiKey = apiKey,
    )

    App()
}
```

```kotlin
App.kt

@Composable
fun App() {
    BotStacksThemeEngine {
        AppNavigation()
    }
}
```

Note, you can optionally delay load and later call `BotStacksChat.shared.load` to load BotStacks in whatever load sequence you wish.

If you'd like Giphy support in your chats, send your Giphy API key during `setup`.

### Step 2: Logging in

Nearly all functionality is within the context of a chat user. That said, you must first be logged in as a chat user in order to appropriately display the UI components.

To log in, call the login function prior to displaying any UI components. Below is an example of How to accomplish this.

```kotlin
val composeScope = rememberCoroutineScope()
composeScope.launch {
    BotStacksChat.shared.login(
        "user-identifier",
        "username"
    ) // optionally pass displayName and picture
    if (BotStacksChat.shared.isUserLoggedIn) {
        // handle logged in state change
    }
}
```

### Step 3: Render the UI

The BotStacks UI Kit uses Jetpack Compose (Multiplatform). There are a plethora of navigation protocols for Compose Multiplatform, so pick whatever one works best for your app.

Our Sample uses [Voyager](https://voyager.adriel.cafe), so we simply make a screen for the controller, drop it in and navigate to it. Our sample makes use of a commonized approach to give iOS and Android both platform specific bottom sheet implementations to keep apps respective of the platform they are on.

```kotlin
data class ChatScreen: Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalPlatformNavigator.current
 +      BotStacksChatController { navigator.replaceAll(LoginScreen) }
    }
}
```

# 🖍 Theming

You can theme your BotStacks UI kit by modifying the defaults of the `BotStacksThemeEngine`. The theme supports fonts, colors, assets, and dimensions. Configure it like this:


```kotlin
BotStacksThemeEngine(
    // true or false to force theming one way (default follows system)
    useDarkTheme = isSystemInDarkTheme(),
    // color scheme for light mode
    lightColorScheme = lightBotStacksColors(
        primary = Purple40,
        onPrimary = Color.White,
    ),
    // color scheme for dark mode
    darkColorScheme = darkBotStacksColors(
        primary = Purple80,
        onPrimary = Color.Black
    ),
    // fonts to utilize for Text within components
    fonts = with(Typography.bodyLarge) {
        botstacksFonts(
            body1 = FontStyle(
                size = fontSize,
            )
        )
    },
    // assets for empty state and logo (in header)
    assets = Assets(
        logo = R.drawable.inappchat_icon,
        emptyChat = EmptyScreenConfig.Messages(
            caption = "No messages yet."
        )
    ),
    // shape definitions for components
    shapes = ShapeDefinitions(
        small = 4.dp,
        medium = 10.dp,
        large = 16.dp
    )
) {
    // content code here (components, Controller)
}
```

# Components & Views

We also support direct component usage in your existing applications. Check out or [components](components.md) and [views](views.md) documentation for integrating them.


