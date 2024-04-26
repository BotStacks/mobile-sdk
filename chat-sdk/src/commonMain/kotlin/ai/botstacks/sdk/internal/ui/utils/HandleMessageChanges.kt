package ai.botstacks.sdk.internal.ui.utils

import ai.botstacks.sdk.internal.utils.ui.isScrolledToTheBeginning
import ai.botstacks.sdk.state.Message
import ai.botstacks.sdk.state.Pager
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Composable
internal fun HandleMessageChanges(
    listState: LazyListState,
    pager: Pager<Message>,
    prefix: List<Message>
) {
    var initialMessageRef by remember(pager) {
        mutableStateOf<Message?>(null)
    }
    var lastMessageSent by rememberSaveable(initialMessageRef) {
        mutableStateOf(initialMessageRef?.createdAt?.toEpochMilliseconds() ?: 0)
    }
    var lastMessageReceived by rememberSaveable(initialMessageRef) {
        mutableStateOf(initialMessageRef?.createdAt?.toEpochMilliseconds() ?: 0)
    }

    // handle incoming/outgoing messages - scroll to bottom to reset view in the following circumstances:
    // 1) New message is from self (e.g outgoing)
    // 2) New message is from participant and we are already at the bottom (to prevent rug pull)
    LaunchedEffect(Unit) {
        snapshotFlow { prefix + pager.items }
            .map { it.firstOrNull() }
            .filterNotNull()
            .onEach {
                if (initialMessageRef == null) {
                    println("initial message ref = ${it.id}")
                    initialMessageRef = it
                }
            }
            .collect { newMessage ->
                println(newMessage)
                val isFromSelf = newMessage.userOrNull?.isCurrent == true
                val isNewerThanLast = newMessage.createdAt.toEpochMilliseconds() > lastMessageSent && initialMessageRef?.id != newMessage.id

                if (isFromSelf && isNewerThanLast) {
                    listState.handleAndReplayAfter(400) {
                        println("new message from self, jumping to bottom")
                        scrollToItem(0)
                        lastMessageSent = newMessage.createdAt.toEpochMilliseconds()
                    }
                } else {
                    listState.handleAndReplayAfter(400) {
                        if (listState.isScrolledToTheBeginning() && isNewerThanLast) {
                            listState.animateScrollToItem(0)
                        }
                        lastMessageReceived = newMessage.createdAt.toEpochMilliseconds()
                    }
                }
            }
    }
}

private suspend fun LazyListState.handleAndReplayAfter(
    delay: Long,
    block: suspend LazyListState.() -> Unit
) {
    delay(delay)
    block()
}