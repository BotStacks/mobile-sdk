package ai.botstacks.sdk.utils

import kotlinx.datetime.Instant
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

fun NSDate.toInstant(): Instant {
    return Instant.fromEpochMilliseconds((NSDate().timeIntervalSince1970 * 1000).toLong())
}