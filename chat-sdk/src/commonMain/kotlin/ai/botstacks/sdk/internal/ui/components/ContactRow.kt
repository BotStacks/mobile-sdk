/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.annotated
import ai.botstacks.sdk.internal.utils.genU
import ai.botstacks.sdk.internal.utils.ift
import ai.botstacks.sdk.state.OnlineStatus
import ai.botstacks.sdk.ui.components.Avatar
import ai.botstacks.`chat-sdk`.generated.resources.Res
import dev.icerock.moko.resources.compose.painterResource

@Composable
internal fun ContactRow(
    user: User,
    modifier: Modifier = Modifier,
    right: @Composable () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(
                start = 16.dp, top = 12.dp, bottom = 12.dp
            )
            .height(84.dp)
    ) {
        Avatar(url = user.avatar)
        Space(8f)
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.username.annotated(),
                    fontStyle = fonts.h3,
                    maxLines = 1,
                    color = colorScheme.onBackground
                )
//                if (user.haveContact) {
                Image(
                    painterResource(Res.images.address_book),
                    contentDescription = "contact",
                    modifier = Modifier.size(18),
                    colorFilter = ColorFilter.tint(Color(0xFF488AC7))
                )
//                }
            }
            Text(
                text = user.status.name.capitalize(Locale.current).annotated(),
                fontStyle = fonts.body1,
                color = ift(
                    user.status == OnlineStatus.Online,
                    colorScheme.primary,
                    colorScheme.caption
                )
            )
        }
        GrowSpacer()
        right()
    }
}

@IPreviews
@Composable
private fun ContactRowPreview() {
    BotStacksThemeEngine {
        Column {
            (0..10).map {
                ContactRow(genU())
            }
        }
    }
}