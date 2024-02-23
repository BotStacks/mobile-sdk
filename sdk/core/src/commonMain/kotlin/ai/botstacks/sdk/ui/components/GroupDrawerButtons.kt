/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.actions.delete
import ai.botstacks.sdk.actions.leave
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.*
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
fun ChatDrawerButtons(
    chat: Chat,
    openEdit: () -> Unit,
    openInvite: (Chat) -> Unit,
    dismiss: () -> Unit,
    back: () -> Unit
) {
    val dialog = remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier
                .height(60.dp)
                .background(colorScheme.surface.copy(0.3f), RoundedCornerShape(16.dp))
                .padding(8.dp, 0.dp)
        ) {
            if (chat.isAdmin) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        dismiss()
                        openEdit()
                    }) {
                    Image(
                        painter = painterResource(Res.drawable.gear_fill),
                        contentDescription = "settings",
                        colorFilter = ColorFilter.tint(colorScheme.border),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Edit", fontStyle = fonts.caption2, color = colorScheme.border)
                }
                Space()
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    dismiss()
                    openInvite(chat)
                }) {
                Image(
                    painterResource(Res.drawable.archive_box_fill),
                    contentDescription = "settings",
                    colorFilter = ColorFilter.tint(colorScheme.border),
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Invite", fontStyle = fonts.caption2, color = colorScheme.border)
            }
            Space()
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    dismiss()
                    dialog.value = true
                }) {
                Image(
                    painter = painterResource(Res.drawable.trash_fill),
                    contentDescription = "settings",
                    colorFilter = ColorFilter.tint(colorScheme.border),
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Leave", fontStyle = fonts.caption2, color = colorScheme.border)
            }
        }
    }
    if (dialog.value) {

        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                dialog.value = false
            },
            title = {
                Text(
                    text = ift(chat.isAdmin, "Delete ${chat.name}", "Leave ${chat.name}"),
                    fontStyle = fonts.h2
                )
            },
            text = {
                Text(
                    "Are you sure you want to ${
                        ift(
                            chat.isAdmin,
                            "delete",
                            "leave"
                        )
                    } this channel?", fonts.body2
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (chat.isAdmin) {
                            op(
                                {
                                    bg {
                                        chat.delete()
                                    }
                                    back()
                                })
                        } else {
                            chat.leave()
                        }
                        dialog.value = false
                        dismiss()
                    }) {
                    Text(
                        ift(chat.isAdmin, "Delete", "Leave"),
                        fonts.body2,
                        color = colorScheme.error
                    )
                }
            },
            dismissButton = {
                Button(

                    onClick = {
                        dialog.value = false
                    }) {
                    Text("Cancel", fonts.body2)
                }
            }
        )
    }
}

@IPreviews
@Composable
fun ChatDrawerButtonsPreview() {
    BotStacksChatContext {
        ChatDrawerButtons(chat = genG(), {}, {}, {}, {})
    }
}