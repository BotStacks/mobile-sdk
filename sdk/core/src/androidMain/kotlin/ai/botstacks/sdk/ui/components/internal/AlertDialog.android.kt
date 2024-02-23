package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.theme.LocalBotStacksColorPalette
import ai.botstacks.sdk.ui.theme.dialogCancelBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialogDefaults.textContentColor
import androidx.compose.material3.AlertDialogDefaults.titleContentColor
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlin.math.max

@Composable
internal actual fun applyAlertActionStyle(
    actionStyle: AlertActionStyle,
    textStyle: TextStyle,
    dark: Boolean
): TextStyle {
    return when (actionStyle) {
        is AlertActionStyle.Default -> {
            textStyle.copy(
                fontWeight = FontWeight.Normal,
                color = BotStacks.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        is AlertActionStyle.Cancel -> {
            textStyle.copy(
                fontWeight = FontWeight.Bold,
                color = LocalBotStacksColorPalette.current.light._900,
                textAlign = TextAlign.Center
            )
        }

        is AlertActionStyle.Destructive -> {
            textStyle.copy(
                fontWeight = FontWeight.Normal,
                color = BotStacks.colorScheme.onError,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal actual fun BotStacksAlertDialog(
    onDismissRequest: () -> Unit,
    title: String?,
    message: String?,
    containerColor: Color,
    buttons: NativeAlertDialogButtonsScope.() -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier,
        properties = DialogProperties()
    ) {
        Surface(
            shape = BotStacks.shapes.large,
            color = BotStacks.colorScheme.surface,
            tonalElevation = 0.0.dp,
            shadowElevation = 6.0.dp
        ) {
            Column(
                modifier = Modifier.padding(PaddingValues(all = BotStacks.dimens.staticGrid.x6))
            ) {
                title?.let {
                    CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                        val textStyle = BotStacks.fonts.h2.asTextStyle()
                        ProvideTextStyle(textStyle) {
                            Box(
                                // Align the title to the center when an icon is present.
                                Modifier
                                    .padding(PaddingValues(bottom = BotStacks.dimens.staticGrid.x4))
                                    .align(Alignment.Start)
                            ) {
                                Text(it)
                            }
                        }
                    }
                }
                message?.let {
                    CompositionLocalProvider(LocalContentColor provides textContentColor) {
                        val textStyle = BotStacks.fonts.body1.asTextStyle()
                        ProvideTextStyle(textStyle) {
                            Box(
                                Modifier
                                    .weight(weight = 1f, fill = false)
                                    .padding(PaddingValues(bottom = BotStacks.dimens.staticGrid.x6))
                                    .align(Alignment.Start)
                            ) {
                                Text(it)
                            }
                        }
                    }
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    NativeAlertDialogButtonsScopeMaterial()
                        .apply(buttons)
                        .Content()
                }
            }
        }
    }
}

private class NativeAlertDialogButtonsScopeMaterial(
) : NativeAlertDialogButtonsScope {

    private val buttons = mutableListOf<@Composable () -> Unit>()

    override fun button(
        onClick: () -> Unit,
        style: AlertActionStyle,
        enabled: Boolean,
        title: String,
    ) {
        buttons.add {
            Box(
                Modifier
                    .clip(CircleShape)
                    .clickable(
                        enabled = enabled,
                        onClick = onClick,
                        role = Role.Button,
                    )
                    .background(
                        when (style) {
                            AlertActionStyle.Cancel -> BotStacks.colorScheme.dialogCancelBackground
                            AlertActionStyle.Default -> Color.Unspecified
                            AlertActionStyle.Destructive -> BotStacks.colorScheme.error
                        }, CircleShape
                    )
                    .padding(ButtonDefaults.TextButtonContentPadding),
                contentAlignment = Alignment.Center,
                content = {
                    Text(
                        text = title,
                        style = style.apply(
                            BotStacks.fonts.button2.asTextStyle(),
                            isSystemInDarkTheme()
                        )
                    )
                }
            )
        }
    }

    @Composable
    fun Content() {
        AlertDialogFlowRow(
            mainAxisSpacing = BotStacks.dimens.staticGrid.x3,
            crossAxisSpacing = BotStacks.dimens.staticGrid.x3
        ) {
            buttons.forEach {
                it()
            }
        }
    }
}

@Composable
private fun AlertDialogFlowRow(
    mainAxisSpacing: Dp,
    crossAxisSpacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val crossAxisSizes = mutableListOf<Int>()
        val crossAxisPositions = mutableListOf<Int>()

        var mainAxisSpace = 0
        var crossAxisSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentMainAxisSize = 0
        var currentCrossAxisSize = 0

        // Return whether the placeable can be added to the current sequence.
        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentMainAxisSize + mainAxisSpacing.roundToPx() +
                    placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                crossAxisSpace += crossAxisSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            crossAxisSizes += currentCrossAxisSize
            crossAxisPositions += crossAxisSpace

            crossAxisSpace += currentCrossAxisSize
            mainAxisSpace = max(mainAxisSpace, currentMainAxisSize)

            currentSequence.clear()
            currentMainAxisSize = 0
            currentCrossAxisSize = 0
        }

        for (measurable in measurables) {
            // Ask the child for its preferred size.
            val placeable = measurable.measure(constraints)

            // Start a new sequence if there is not enough space.
            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            // Add the child to the current sequence.
            if (currentSequence.isNotEmpty()) {
                currentMainAxisSize += mainAxisSpacing.roundToPx()
            }
            currentSequence.add(placeable)
            currentMainAxisSize += placeable.width
            currentCrossAxisSize = max(currentCrossAxisSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val mainAxisLayoutSize = max(mainAxisSpace, constraints.minWidth)

        val crossAxisLayoutSize = max(crossAxisSpace, constraints.minHeight)

        val layoutWidth = mainAxisLayoutSize

        val layoutHeight = crossAxisLayoutSize

        layout(layoutWidth, layoutHeight) {
            sequences.forEachIndexed { i, placeables ->
                val childrenMainAxisSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                            if (j < placeables.lastIndex) mainAxisSpacing.roundToPx() else 0
                }
                val arrangement = Arrangement.End
                val mainAxisPositions = IntArray(childrenMainAxisSizes.size) { 0 }
                with(arrangement) {
                    arrange(
                        mainAxisLayoutSize, childrenMainAxisSizes,
                        layoutDirection, mainAxisPositions
                    )
                }
                placeables.forEachIndexed { j, placeable ->
                    placeable.place(
                        x = mainAxisPositions[j],
                        y = crossAxisPositions[i]
                    )
                }
            }
        }
    }
}