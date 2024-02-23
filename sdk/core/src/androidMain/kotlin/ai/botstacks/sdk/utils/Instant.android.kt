package ai.botstacks.sdk.utils

import android.text.format.DateFormat
import android.text.format.DateUtils
import io.ktor.util.date.getTimeMillis
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.Calendar
import java.util.Locale

actual fun Instant.relativeTimeString(): String {
    return DateUtils.getRelativeTimeSpanString(
        this.toEpochMilliseconds(),
        Clock.System.now().toEpochMilliseconds(),
        0,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

actual fun Instant.relativeTimeString(other: Instant): String {
    return DateUtils.getRelativeTimeSpanString(
        this.toEpochMilliseconds(),
        other.toEpochMilliseconds(),
        0,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
}

actual fun Instant.format(format: String): String {
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = toEpochMilliseconds()
    return DateFormat.format(format, calendar).toString()
}