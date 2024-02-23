/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.botstacks.sdk.state.BotStacksChatStore
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.ift
import co.touchlab.kermit.Logger
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmojiKeyboard(onEmoji: (String) -> Unit = {}) {
//    val emojiBottomSheetDialogFragment = EmojiBottomSheet.newInstance({ emoji ->
//        onEmoji(emoji.emoji)
//    })
//    (LocalContext.current as? AppCompatActivity)?.supportFragmentManager?.let {
//        emojiBottomSheetDialogFragment.show(it, EmojiBottomSheet.TAG)
//    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmojiBar(
    current: String? = null,
    onEmoji: (String) -> Unit = { Logger.d("Got Emoji $it") }
) {
    var keyboard = remember {
        false
    }
    val emojis = BotStacksChatStore.current.settings.lastUsedReactions.toMutableList()
    current?.let {
        if (!emojis.contains(it)) {
            emojis.add(0, it)
            emojis.removeLast()
        }
    }
    if (keyboard) {
        EmojiKeyboard(onEmoji)
        return
    }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        emojis.map { emoji ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .circle(40.dp, colorScheme.surface)
                    .border(
                        2.dp,
                        ift(current == emoji, colorScheme.primary, Color.Transparent),
                        CircleShape
                    )
                    .clickable { onEmoji(emoji) }
            ) {
                androidx.compose.material3.Text(text = emoji, fontSize = 18.sp)
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .circle(40.dp, colorScheme.surface)
                .clickable { keyboard = true }
        ) {
            Icon(
                painter = painterResource(Res.drawable.plus),
                contentDescription = "more reactions",
                tint = colorScheme.onBackground,
                modifier = Modifier.size(
                    20
                )
            )
        }
    }
}

@IPreviews
@Composable
fun EmojiBarPreviews() {
    BotStacksChatContext {
        EmojiBar(current = "🤟")
    }
}
