package ai.botstacks.sdk.ui.components.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
internal expect fun ImageRenderer(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    url: String,
    onClick: (() -> Unit)? = null,
    onLongClick: () -> Unit,
)