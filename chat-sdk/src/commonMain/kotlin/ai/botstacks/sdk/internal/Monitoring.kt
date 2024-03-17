/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.internal

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.LogLevel
import ai.botstacks.sdk.SdkConfig

internal val Monitor
    get() = BotStacksChat.monitoring

internal class Monitoring(
    private val level: LogLevel = LogLevel.NONE,
    private val log: (String) -> Unit = { println(it) }
) {
    private val printLogs = SdkConfig.DEBUG ||  level != LogLevel.NONE

    fun debug(message: String, data: Map<String, Any>? = null) {
        if (printLogs) {
            if (level == LogLevel.DEBUG || level == LogLevel.VERBOSE) {
                log(message + " ${data?.entries?.joinToString().orEmpty()}")
            }
        }
    }

    fun network(message: String) {
        if (printLogs) {
            if (level == LogLevel.NETWORK_ONLY || level == LogLevel.VERBOSE) {
                log(message)
            }
        }
    }

    fun info(message: String, data: Map<String, Any>? = null) {
        if (printLogs) {
            if (level == LogLevel.INFO || level == LogLevel.VERBOSE) {
                log(message + " ${data?.entries?.joinToString().orEmpty()}")
            }
        }
    }

    fun warning(message: String, data: Map<String, Any>? = null) {
        if (printLogs) {
            if (level == LogLevel.WARNING || level == LogLevel.VERBOSE) {
                log(message + " ${data?.entries?.joinToString().orEmpty()}")
            }
        }
    }

    fun error(error: Throwable, message: String? = null) {
        if (printLogs) {
            if (level == LogLevel.ERROR || level == LogLevel.VERBOSE) {
                if (message != null) {
                    log(message)
                }
                log(error.stackTraceToString())
            }
        }
    }

    fun error(message: String, data: Map<String, Any>? = null) {
        if (printLogs) {
            if (level == LogLevel.ERROR || level == LogLevel.VERBOSE) {
                log(message + " ${data?.entries?.joinToString().orEmpty()}")
            }
        }
    }
}
