package ai.botstacks.sdk.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime


expect fun Instant.relativeTimeString(): String
expect fun Instant.relativeTimeString(other: Instant): String

fun Instant.minutesBetween(other: Instant): Int = other.minus(this, DateTimeUnit.MINUTE).toInt()
fun Instant.hoursBetween(other: Instant): Int = other.minus(this, DateTimeUnit.HOUR).toInt()
fun Instant.daysBetween(other: Instant): Int = other.minus(this, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toInt()

expect fun Instant.format(format: String): String