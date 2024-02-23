/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.theme.EmptyScreenConfig
import ai.botstacks.sdk.ui.theme.defaultImage
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.ui.painterImageAsset
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


data class CTA(val icon: Painter?, val text: String, val to: () -> Unit)


@Composable
fun EmptyListView(
  config: EmptyScreenConfig,
  cta: CTA? = null,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
      Spacer(modifier = Modifier.weight(1f))
      (config.image()?.let { painterImageAsset(it) } ?: config.defaultImage)?.let { painter ->
        Image(painter = painter, contentDescription = "empty list")
      }
      Space(8f)
      config.caption?.let {
        Text(
          text = it,
          fontStyle = fonts.h2.copy(weight = FontWeight.Normal),
          textAlign = TextAlign.Center,
          color = colorScheme.onBackground
        )
      }
      Spacer(modifier = Modifier.weight(1f))
      cta?.let {
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier
          .clickable { it.to() }
          .padding(32.dp, 0.dp)) {
          Row(
            modifier = Modifier
              .height(60.dp)
              .border(2.dp, colorScheme.onBackground, RoundedCornerShape(30.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Spacer(modifier = Modifier.weight(1f))
            it.icon?.let {
              Image(
                painter = it,
                contentDescription = "call to action",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(colorScheme.onBackground)
              )
            }
            Text(text = it.text.uppercase(), fontStyle = fonts.h2, color = colorScheme.onBackground)
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@IPreviews
@Composable
fun EmptyListViewPreview() {
  BotStacksChatContext {
    EmptyListView(config = BotStacks.assets.emptyAllChannels, cta = CTA(null, "Create a Channel", {}))
  }
}