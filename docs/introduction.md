![Maven Central](https://img.shields.io/maven-central/v/io.inappchat/sdk) ![GitHub issues](https://img.shields.io/github/issues/inappchat/android-example)

![BotStacks](botstacks-logo.png)


# BotStacks Kotlin Multiplatform SDK

> Delightful chat for your Android apps

Try the demo, download [BotStacks Android](https://play.google.com/store/apps/details?id=ai.botstacks.sample)

&nbsp;

# 📃 Table of Contents

- [Overview](#-overview)
- [Installation](#-installation)
- [Help](#-help)

&nbsp;

# ✨ Overview

This SDK integrates a fully serviced chat experience on the [BotStacks](https://botstacks.ai) platform.

&nbsp;

# ⚙ Installation

### Compose Multiplatform

Add `ai.botstacks:chat-sdk:{version}` to your dependencies

```gradle
val commonMain by getting {
    dependencies {
        [...]
+       implementation("ai.botstacks:chat-sdk:{version}")
        [...]
    }
}
```

### Android Only

If you are only targeting Android the dependency is:

```gradle
dependencies {
    [...]
+   implementation("ai.botstacks:chat-sdk-android:{version}")
    [...]
}
```

&nbsp;

# 🙋‍♂️ Help

If you don't understand something in the documentation, you are experiencing problems, or you just need a gentle nudge in the right direction, please join our [Discord server](https://discord.com/invite/5kwyQCz3zZ)

---

**All Content Copyright © 2023 BotStacks**
