/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genG
import botstacks.sdk.core.generated.resources.Res
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ChatDrawerHeader(chat: Chat) {
    Column {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(64.dp, 24.dp, 64.dp, 24.dp)
        ) {
            Space(24f)
            Avatar(url = chat.displayImage, size = AvatarSize.Large, chat = true)
            Space(12f)
            Text(chat.displayName, fonts.h2, color = colorScheme.onBackground)
            Text(chat.displayDescription ?: "", fonts.body1, color = colorScheme.caption)
            Space(26f)
            Divider(color = colorScheme.onBackground.copy(alpha = 0.1f))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "All Members", fontStyle = fonts.body2, color = colorScheme.onBackground)
            Space(14f)
            Image(
                painter = painterResource(Res.drawable.users_three_fill),
                contentDescription = "member count",
                colorFilter = ColorFilter.tint(colorScheme.caption),
                modifier = Modifier.size(16)
            )
            Space()
            Text(chat.members.size.toString(), fonts.caption1, color = colorScheme.caption)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ChatDrawer(
    chat: Chat?,
    state: ModalBottomSheetState,
    hide: () -> Unit,
    openEdit: () -> Unit,
    openInvite: (Chat) -> Unit,
    openProfile: (User) -> Unit,
    back: () -> Unit,
    content: @Composable () -> Unit
) {
    if (chat == null) return content()
    ModalBottomSheetLayout(
        sheetState = state,
        modifier = Modifier.fillMaxSize(),
        sheetBackgroundColor = colorScheme.background,
        sheetContentColor = colorScheme.onBackground,
        scrimColor = colorScheme.scrim,
        sheetContent = {
            Box(contentAlignment = Alignment.BottomCenter) {
                Column {
                    ChatDrawerHeader(chat = chat)
                    val headers = mapOf(
                        "Admins" to chat.admins,
                        "Online" to chat.onlineNotAdminUsers,
                        "Offline" to chat.offlineNotAdminUsers
                    )
                    LazyColumn {
                        headers.forEach { (name, users) ->
                            stickyHeader {
                                Text(
                                    text = name.uppercase(),
                                    fontStyle = fonts.caption1.copy(weight = FontWeight.Bold),
                                    color = colorScheme.caption,
                                    modifier = Modifier.padding(top = 24.dp, start = 16.dp)
                                )
                            }
                            items(users, { it.user_id }) {
                                ContactRow(user = it.user, modifier = Modifier.clickable {
                                    hide()
                                    openProfile(it.user)
                                })
                            }
                        }
                    }
                }
                ChatDrawerButtons(
                    chat = chat,
                    openEdit = openEdit,
                    openInvite = openInvite,
                    dismiss = hide,
                    back = back
                )
            }
        },
        content = content
    )
}

@OptIn(ExperimentalMaterialApi::class)
@IPreviews
@Composable
fun ChatDrawerPreview() {
    BotStacksChatContext {
        var open = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Expanded,
            skipHalfExpanded = true
        )
        val coroutineContext = rememberCoroutineScope()
        ChatDrawer(chat = genG(), open, {}, {}, {}, {}, {}) {
            ClickableText(
                text = "hello",
                iac = fonts.body1,
                color = colorScheme.onBackground,
                onClick = { coroutineContext.launch { open.show() } })
        }
    }
}