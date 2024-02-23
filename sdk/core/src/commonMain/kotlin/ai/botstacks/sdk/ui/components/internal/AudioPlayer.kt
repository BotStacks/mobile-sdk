/*
 * Copyright (c) 2023.
 */



package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.utils.ift
import androidx.compose.runtime.Composable

//class AudioUrlProvider(override val values: Sequence<String> = sequenceOf("https://file-examples.com/storage/fe0358100863d05afed02d2/2017/11/file_example_MP3_5MG.mp3")) :
//    PreviewParameterProvider<String>


@Composable
expect fun AudioPlayer(url: String)

fun msToString(_time: Long): String {
    var time = (_time / 1000L)
    if (time == 0L) {
        return ""
    }
    val seconds = time % 60
    time = (time - seconds) / 60
    val minutes = time % 60
    val hours = (time - minutes) / 60
    var ret = ""
    if (hours > 0) {
        ret += "$hours:"
    }
    ret += "$minutes:"
    return ret + ift(seconds < 10, "0$seconds", "$seconds")
}