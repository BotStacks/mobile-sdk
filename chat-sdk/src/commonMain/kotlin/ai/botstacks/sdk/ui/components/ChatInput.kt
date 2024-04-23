/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.internal.actions.send
import ai.botstacks.sdk.internal.ui.components.Pressable
import ai.botstacks.sdk.internal.ui.components.TextInput
import ai.botstacks.sdk.state.Chat
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.dimens
import ai.botstacks.sdk.ui.BotStacksThemeEngine
import ai.botstacks.sdk.internal.utils.IPreviews
import ai.botstacks.sdk.internal.utils.genChat
import ai.botstacks.sdk.internal.utils.ui.keyboardAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import ai.botstacks.`chat-sdk`.generated.resources.Res
import ai.botstacks.sdk.state.Message
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ChatInput
 *
 * Text input that handles the sending of messages to a given [Chat] when the send button is pressed.
 * This is generally used for [MessageList] as there is handling for an attachment sheet that will present
 * from the callback [onMedia].
 *
 * @param modifier The modifier for this ChatInput
 * @param chat The chat associated with this input
 * @param onMedia when the media button is pressed.
 *@param focusRequester An optional focus requester if you need to react to changes in focus of the
 * TextInput.
 */
@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    message: Message,
    onMedia: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    ChatInput(
        modifier = modifier,
        chat = message.chat,
        inReplyTo = message.id,
        onMedia = onMedia,
        focusRequester = focusRequester
    )
}

/**
 * ChatInput
 *
 * Text input that handles the sending of messages to a given [Chat] when the send button is pressed.
 * This is generally used for [MessageList] as there is handling for an attachment sheet that will present
 * from the callback [onMedia].
 *
 * @param modifier The modifier for this ChatInput
 * @param chat The chat associated with this input
 * @param onMedia when the media button is pressed.
 *@param focusRequester An optional focus requester if you need to react to changes in focus of the
 * TextInput.
 */
@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    chat: Chat,
    onMedia: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    ChatInput(
        modifier = modifier,
        chat = chat,
        inReplyTo = null,
        onMedia = onMedia,
        focusRequester = focusRequester
    )
}

@Composable
internal fun ChatInput(
    modifier: Modifier = Modifier,
    chat: Chat,
    inReplyTo: String?,
    onMedia: () -> Unit,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val composeScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(TextFieldValue()) }
    val keyboardVisible by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val onSend = {
        if (state.text.isNotBlank()) {
            composeScope.launch {
                val text = state.text
                if (keyboardVisible) {
                    keyboardController?.hide()
                    delay(300)
                }
                state = TextFieldValue()
                chat.send(inReplyTo = inReplyTo, text)
            }
        }
    }

    Row(
        modifier = modifier
//            .clickable { keyboardController?.show() }
            .imePadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.grid.x2),
    ) {
        TextInput(
            modifier = Modifier
                .background(colorScheme.chatInput, BotStacks.shapes.medium)
                .padding(dimens.grid.x3)
                .weight(1f)
                .focusRequester(focusRequester),
            value = state,
            onValueChanged = { state = it },
            color = colorScheme.onChatInput,
            keyboardActions = KeyboardActions(onDone = { onSend() }),
            placeholder = "Message...",
            leadingIcon = {
                Pressable(
                    onClick = {
                        keyboardController?.hide()
                        composeScope.launch {
                            delay(300)
                            onMedia()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.images.paperclip_fill),
                        contentDescription = "send attachment",
                        modifier = Modifier.requiredIconSize(),
                        tint = colorScheme.caption
                    )
                }
            },
        )
        val canSend by remember {
            derivedStateOf { state.text.isNotBlank() }
        }

        Pressable(enabled = canSend, onClick = onSend) {
            val color by animateColorAsState(
                targetValue = if (canSend) colorScheme.primary else colorScheme.surface,
                label = "send button bg color"
            )
            val contentColor by animateColorAsState(
                targetValue = if (canSend) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                label = "send button content color"
            )
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .padding(dimens.grid.x3),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(Res.images.send),
                    contentDescription = "send message",
                    modifier = Modifier.requiredIconSize(),
                    tint = contentColor
                )
            }
        }
    }
}

@IPreviews
@Composable
private fun MessageInputPreview() {
    BotStacksThemeEngine {
        ChatInput(chat = genChat(), onMedia = {})
    }

}