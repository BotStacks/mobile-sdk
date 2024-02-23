/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.type.NotificationSetting
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.GrowSpacer
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.Space
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.utils.IPreviews

@Composable
fun Option(name: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .padding(16.dp)
    ) {
        Text(name, fontStyle = fonts.body2, color = colorScheme.onBackground)
        GrowSpacer()
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = colorScheme.primary)
        )
    }
}

@Composable
fun NotificationSettingsView(back: () -> Unit) {
    val settings = BotStacksChatStore.current.settings
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Header(title = "Manage Notifications", onBackClicked = back)
        Row(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = "Notification Status", fontStyle = fonts.body1, color = colorScheme.onBackground)
        }
        Space(20f)
        Option(
            name = "Allow All",
            selected = settings.notifications == NotificationSetting.all
        ) {
            settings.setNotifications(NotificationSetting.all)
        }
        Option(
            name = "Mentions Only",
            selected = settings.notifications == NotificationSetting.none
        ) {
            settings.setNotifications(NotificationSetting.mentions)
        }
        Option(
            name = "None",
            selected = settings.notifications == NotificationSetting.none
        ) {
            settings.setNotifications(NotificationSetting.none)
        }
        GrowSpacer()
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .clickable { back() }
                    .height(60.dp)
                    .background(colorScheme.primary, RoundedCornerShape(30.dp))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(text = "Update".uppercase(), fontStyle = fonts.body2, color = colorScheme.background)
            }
        }
    }
}

@IPreviews
@Composable
fun NotificationSettingsPreview() {
    BotStacksChatContext {
        NotificationSettingsView {

        }
    }
}