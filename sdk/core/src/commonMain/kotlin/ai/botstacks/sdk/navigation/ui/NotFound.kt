/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.utils.IPreviews

@Composable
fun NotFound(what: String = "Page", back: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(title = what, onBackClicked = back)
        Column(
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "The page you were looking for could not be found",
                fontStyle = fonts.h2,
                color = colorScheme.onBackground
            )
        }
    }
}

@IPreviews
@Composable
fun NotFoundPreview() {
    BotStacksChatContext {
        NotFound {

        }
    }
}