package ai.botstacks.sdk.internal.navigation.ui.chats

import ai.botstacks.sdk.internal.ui.components.ScrollStartPosition
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.ChatInput
import ai.botstacks.sdk.ui.components.ChatMessage
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.LocalThreaded
import ai.botstacks.sdk.ui.components.MediaActionSheet
import ai.botstacks.sdk.ui.components.MessageActionSheet
import ai.botstacks.sdk.ui.components.ThreadMessageList
import ai.botstacks.sdk.ui.components.rememberMediaActionSheetState
import ai.botstacks.sdk.ui.components.rememberMessageActionSheetState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch


@Composable
internal fun ThreadedRepliesScreen(
    modifier: Modifier = Modifier,
    message: Message,
    onBackClicked: () -> Unit,
    openProfile: (User) -> Unit,
) {
    val mediaSheetState = rememberMediaActionSheetState(message = message)
    val messageActionSheetState = rememberMessageActionSheetState()

    val composeScope = rememberCoroutineScope()

    MediaActionSheet(state = mediaSheetState) {
        CompositionLocalProvider(LocalThreaded provides true) {
            MessageActionSheet(state = messageActionSheetState, {}) {
                Column(modifier = modifier.fillMaxSize()) {
                    CompositionLocalProvider(LocalThreaded provides false) {
                        ThreadMessageList(
                            modifier = Modifier.weight(1f),
                            message = message,
                            header = {
                                Header(
                                    title = "Thread",
                                    onBackClicked = onBackClicked,
                                )
                            },
                            emptyState = {},
                            initialPosition = ScrollStartPosition.Beginning,
                            contentHeader = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(BotStacks.dimens.inset)
                                ) {
                                    CompositionLocalProvider(LocalThreaded provides true) {
                                        ChatMessage(
                                            modifier = Modifier.padding(horizontal = BotStacks.dimens.inset),
                                            message = message,
                                            showAvatar = false,
                                            onLongPress = {
                                                messageActionSheetState.messageForAction = message
                                            },
                                            onPressUser = { }
                                        )
                                    }
                                    Divider(
                                        modifier = Modifier
                                            .padding(horizontal = BotStacks.dimens.inset)
                                            .padding(bottom = BotStacks.dimens.inset),
                                    )
                                }
                            },
                            onPressUser = { openProfile(it) },
                            onLongPress = { messageActionSheetState.messageForAction = it },
                        )
                    }
                    ChatInput(
                        modifier = Modifier.padding(BotStacks.dimens.grid.x4)
                            .navigationBarsPadding(),
                        message = message,
                        onMedia = { composeScope.launch { mediaSheetState.show() } }
                    )
                }
            }
        }
    }
}