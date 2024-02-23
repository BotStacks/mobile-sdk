/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.utils

import ai.botstacks.sdk.BotStacksChat
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun launch(
    context: CoroutineContext = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    BotStacksChat.shared.scope.launch(context, start, block)
}

private val EmptyFn: () -> Unit = {}

fun op(
    block: suspend CoroutineScope.() -> Unit,
    onError: () -> Unit = EmptyFn,
    context: CoroutineContext = Dispatchers.Main
) = launch(context) {
    try {
        block()
    } catch (err: Exception) {
        Monitoring.error(err)
        onError.invoke()
    } catch (err: Error) {
        Monitoring.error(err)
        onError.invoke()
    }
}

fun op(block: suspend CoroutineScope.() -> Unit, onError: () -> Unit = EmptyFn) =
    op(block, onError, Dispatchers.Main)


suspend fun <T> bg(block: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, block)
fun opbg(block: suspend CoroutineScope.() -> Unit, onError: () -> Unit = EmptyFn) =
    op(block, onError, Dispatchers.IO)

fun <T : Unit> async(block: suspend CoroutineScope.() -> T) =
    launch(Dispatchers.IO, block = block)

suspend fun <T> retryIO(
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
            println(e)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // last attempt
}
