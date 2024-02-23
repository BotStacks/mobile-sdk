package ai.botstacks.sdk.utils

import kotlinx.datetime.Instant

fun String.instant() = Instant.parse(this)