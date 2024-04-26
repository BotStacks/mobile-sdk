/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal.utils

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.internal.Monitor

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

internal fun launch(
    context: CoroutineContext = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    BotStacksChat.shared.scope.launch(context, start, block)
}

internal fun op(
    block: suspend CoroutineScope.() -> Unit,
    onError: (Throwable) -> Unit = {  },
    context: CoroutineContext = Dispatchers.Main
) = launch(context) {
    try {
        block()
    } catch (err: Exception) {
        Monitor.error(err)
        onError.invoke(err)
    } catch (err: Error) {
        Monitor.error(err)
        onError.invoke(err)
    }
}

internal fun op(block: suspend CoroutineScope.() -> Unit, onError: (Throwable) -> Unit = { }) =
    op(block, onError, Dispatchers.Main)


internal suspend fun <T> bg(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)
internal fun opbg(onError: (Throwable) -> Unit = { }, block: suspend CoroutineScope.() -> Unit) =
    op(block, onError, Dispatchers.IO)

internal fun <T : Unit> async(block: suspend CoroutineScope.() -> T) =
    launch(Dispatchers.IO, block = block)

internal suspend fun <T> retryIO(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 100, // 0.1 second
    maxDelay: Long = 1000,    // 1 second
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            // you can log an error here and/or make a more finer-grained
            // analysis of the cause to see if retry is needed
            Monitor.error(e)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // last attempt
}
