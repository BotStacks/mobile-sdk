# Setup

## Quick Start

In order to display any of the UI components and access chat data, you must first initialize the SDK and log in as a chat user.

### Step 1: Initialize the SDK

In your Application class, call BotStacksChat.shared.setup with your API key. You can obtain your API key from the [Botstacks Dashboard](https://dashboard.botstacks.ai/settings). If you don’t yet have one, you can [create one for FREE](https://dashboard.botstacks.ai/)!

If you don't have an Application class, [create one](https://guides.codepath.com/android/Understanding-the-Android-Application-Class).

```kotlin
class App : Application() {

    override fun onCreate() {
        super.onCreate()
+       BotStacksChat.shared.setup(
+           context = this,
+           apiKey = "your-api-key"
+       )
    }
}
```

Note, you can optionally delay load and later call `BotStacksChat.shared.load` to load BotStacks in whatever load sequence you wish.

If you'd like Giphy support in your chats, send your Giphy API key during `setup`.

```kotlin
BotStacksChat.shared.setup(
    context = this,
    apiKey = "your_botstacks_api_key",
    giphyApiKey = "your_giphy_api_key"
)
```

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

The BotStacks UI Kit uses Jetpack Compose.
You can add it to any `NavHost` by rendering inside an `BotStacksThemeEngine` and adding the `BotStacksChatController`. Customization controls for the Theme Engine are described below.

```kotlin
BotStacksThemeEngine {
    NavHost(navController = navController, startDestination = "splash") {
        val openChat = {
            navController.navigate("chats")
        }
        composable("splash") {
            Splash(openChat = openChat, openLogin = {
                navController.navigate("login")
            })
        }
        composable("login") {
            Login(openChat)
        }

        composable("chats") {
           BotStacksChatController(onLogout = { navController.navigate("login") })
        }
    }
}
```

&nbsp;

## Step 4: Push Notifications (Firebase Cloud Messaging)

For push notifications via FCM, just pass your push token to BotStacks

```kotlin
BotStacksChat.registerFCMToken(token)
```

&nbsp;

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

We also support direct component usage in your existing applications. Check out or [components](../compose/components.md) and [views](../compose/views.md) documentation for integrating them.