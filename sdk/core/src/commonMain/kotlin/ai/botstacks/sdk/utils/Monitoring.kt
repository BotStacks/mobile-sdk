/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.utils


import co.touchlab.kermit.Logger
import io.sentry.kotlin.multiplatform.Sentry

object Monitoring {
    fun setup() {
        Sentry.init {
            it.dsn = "https://17891a46f1414379ab8dee14743c15a6@o4505121822801920.ingest.sentry.io/4505121983168512"
        }
    }

    fun error(message: String, data: Map<String, Any>? = null) {
        println(message + " ${data?.entries?.joinToString()}")
        Sentry.captureMessage(
            if (data != null) message + " Data: ${data.entries.joinToString()}" else message,
        )
    }

    fun error(error: Throwable, data: Map<String, Any>? = null) {
        println(error.stackTraceToString())
        if (data != null) {
            println(data.entries.joinToString())
            Sentry.captureMessage("Error data: " + data.entries.joinToString())
        }
        Sentry.captureException(error)
    }

    fun log(message: String, data: Map<String, Any>? = null) {
        Logger.d(message + " ${data?.entries?.joinToString()}")
        Sentry.captureMessage(
            if (data != null) message + " Data: ${data.entries.joinToString()}" else message,
        )
    }

    fun info(message: String, data: Map<String, Any>? = null) {
        println(message + " ${data?.entries?.joinToString()}")
        Sentry.captureMessage(
            if (data != null) message + " Data: ${data.entries.joinToString()}" else message,
        )
    }

    fun warning(message: String, data: Map<String, Any>? = null) {
        println(message + " ${data?.entries?.joinToString()}")
        Sentry.captureMessage(
            if (data != null) message + " Data: ${data.entries.joinToString()}" else message,
        )
    }

    fun critical(message: String, data: Map<String, Any>? = null) {
        println(message + " ${data?.entries?.joinToString()}")
        Sentry.captureMessage(
            if (data != null) message + " Data: ${data.entries.joinToString()}" else message,
        )
    }
}
