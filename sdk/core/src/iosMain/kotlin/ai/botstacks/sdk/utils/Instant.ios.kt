package ai.botstacks.sdk.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toNSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSRelativeDateTimeFormatter
import platform.Foundation.NSRelativeDateTimeFormatterUnitsStyleAbbreviated
import platform.Foundation.NSRelativeDateTimeFormatterUnitsStyleFull

actual fun Instant.relativeTimeString(): String {
    val formatter = NSRelativeDateTimeFormatter().apply {
        dateTimeStyle = NSRelativeDateTimeFormatterUnitsStyleFull
    }

    return formatter.localizedStringForDate(this.toNSDate(), Clock.System.now().toNSDate())
}

actual fun Instant.relativeTimeString(other: Instant): String {
    val formatter = NSRelativeDateTimeFormatter().apply {
        dateTimeStyle = NSRelativeDateTimeFormatterUnitsStyleAbbreviated
    }

    return formatter.localizedStringForDate(this.toNSDate(), other.toNSDate())
}

actual fun Instant.format(format: String): String {
    val dateFormatter = NSDateFormatter().apply {
        dateFormat = format
    }

    return dateFormatter.stringFromDate(toNSDate())
}