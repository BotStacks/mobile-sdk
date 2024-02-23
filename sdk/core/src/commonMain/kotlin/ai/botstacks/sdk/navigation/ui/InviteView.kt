/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui

import ai.botstacks.sdk.API
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
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.actions.invite
import ai.botstacks.sdk.actions.update
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.state.Upload
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.ContactRow
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.PagerList
import ai.botstacks.sdk.ui.components.PagerListDefaults
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.ui.components.circle
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genG
import ai.botstacks.sdk.utils.genU
import ai.botstacks.sdk.utils.op
import ai.botstacks.sdk.utils.random
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Stable
data class CreateChatState(val chat: Chat? = null) {
    var upload by mutableStateOf<Upload?>(null)
    var image: String? = chat?.image

    @OptIn(ExperimentalFoundationApi::class)
    val name = TextFieldState(chat?.name.orEmpty())
    var description = TextFieldState(chat?.description.orEmpty())
    var _private by mutableStateOf(chat?._private ?: false)

    init {
        current = this
    }

    var selectImage by mutableStateOf(false)
    val executing: Boolean get() = creating || chat?.updating ?: false
    var creating by mutableStateOf(false)
    fun exec(openInvite: (Chat) -> Unit, back: () -> Unit) {
        if (executing) return
        if (chat != null) {
            op({
                val img = upload?.await()
                chat.update(name.text.toString(), description.text.toString(), img, _private) {
                    back()
                }
            })
        } else {
            op({
                creating = true
                val img = upload?.await()
                val chat =
                    API.createChat(
                        name.text.toString(),
                        description = description.text.toString(),
                        _private = _private,
                        image = img
                    )
                creating = false
                chat?.let {
                    newChats.add(it.id)
                    back()
                    openInvite(it)
                }
            }) {
                creating = false
            }

        }
    }

    companion object {
        var current by mutableStateOf<CreateChatState?>(null)
        var newChats = mutableSetOf<String>()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun InviteView(chat: Chat, back: () -> Unit, openChat: (Chat) -> Unit) {
    val selected = remember {
        mutableStateListOf<User>()
    }

    Column {
        Header(title = "Invite to ${chat.name}", onBackClicked = back)
        PagerList(
            pager = BotStacksChatStore.current.contacts,
            separator = { before, after ->
                if (before != null) PagerListDefaults.Divider()
            },
            modifier = Modifier.weight(1f)
        ) {
            val isSelected = selected.contains(it)
            ContactRow(user = it, modifier = Modifier
                .padding(end = 16.dp)
                .fillMaxWidth()
                .clickable { if (!isSelected) selected.add(it) else selected.remove(it) }) {
                Box(
                    modifier =
                    Modifier.circle(25.dp, if (isSelected) colorScheme.primary else colorScheme.caption),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = "Check mark",
                        tint = colorScheme.background,
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
                    .circle(50.dp, colorScheme.caption)
                    .clickable { back() }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.caret_left),
                    contentDescription = "back",
                    tint = colorScheme.background,
                    modifier = Modifier.size(22.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f)
                    .background(
                        if (selected.isNotEmpty()) colorScheme.primary else colorScheme.caption,
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
                Text(text = "Invite Friends", fontStyle = fonts.body2, color = colorScheme.background)
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