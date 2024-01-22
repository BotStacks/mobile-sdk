/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.actions.invite
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.IAC.colors
import ai.botstacks.sdk.ui.IAC.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.views.*
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genG
import ai.botstacks.sdk.utils.genU
import ai.botstacks.sdk.utils.random

@Composable
fun InviteView(chat: Chat, back: () -> Unit, openChat: (Chat) -> Unit) {
    val selected = remember {
        mutableStateListOf<User>()
    }

    Column {
        Header(title = "Invite to ${chat.name}", back = back)
        PagerList(
            pager = BotStacksChatStore.current.contacts,
            divider = true,
            modifier = Modifier.weight(1f)
        ) {
            val isSelected = selected.contains(it)
            ContactRow(user = it, modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth()
                .clickable { if (!isSelected) selected.add(it) else selected.remove(it) }) {
                Box(
                    modifier =
                    Modifier.circle(25.dp, if (isSelected) colors.primary else colors.caption),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = ai.botstacks.sdk.R.drawable.check),
                        contentDescription = "Check mark",
                        tint = colors.background,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(20.dp, 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .circle(50.dp, colors.caption)
                    .clickable { back() }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = ai.botstacks.sdk.R.drawable.caret_left),
                    contentDescription = "back",
                    tint = colors.background,
                    modifier = Modifier.size(22.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)
                    .background(
                        if (selected.isNotEmpty()) colors.primary else colors.caption,
                        RoundedCornerShape(25.dp)
                    )
                    .clickable {
                        if (selected.isNotEmpty() && !chat.inviting) {
                            chat.invite(selected)
                            back()
                            if (CreateChatState.newChats.contains(chat.id)) {
                                CreateChatState.newChats.remove(chat.id)
                                openChat(chat)
                            }
                        }
                    }) {
                Text(text = "Invite Friends", iac = fonts.headline, color = colors.background)
            }
        }
    }
}

@IPreviews
@Composable
fun InvitePreview() {
    BotStacksChatContext {
        BotStacksChatStore.current.contacts.items.addAll(random(20, { genU() }))
        InviteView(chat = genG(), {}) {

        }
    }
}