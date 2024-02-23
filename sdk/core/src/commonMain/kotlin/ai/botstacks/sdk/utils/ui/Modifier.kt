package ai.botstacks.sdk.utils.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

fun Modifier.measured(block: (DpSize) -> Unit): Modifier = composed {
    val density = LocalDensity.current
    onPlaced { block(it.size.toDp(density)) }
}

fun Modifier.debugBounds(
    width: Dp = 1.dp,
    color: Color = Color.Magenta,
    shape: Shape = RectangleShape,
) = this.border(width = width, color = color, shape = shape)

fun Modifier.unboundedClickable(
    enabled: Boolean = true,
    role: Role = Role.Button,
    interactionSource: MutableInteractionSource? = null,
    rippleRadius: Dp = 24.dp,
    onClick: () -> Unit,
) = this.composed {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }

    clickable(
        onClick = onClick,
        enabled = enabled,
        role = role,
        interactionSource = interaction,
        indication = rememberRipple(bounded = false, radius = rippleRadius),
    )
}

fun Modifier.disableInput(disabled: Boolean): Modifier {
    return if (disabled) {
        pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }
}

fun Modifier.addIf(condition: Boolean, other: @Composable () -> Modifier): Modifier = composed {
    then(if (condition) other() else Modifier)
}

fun <T> Modifier.addIfNonNull(value: T?, other: @Composable (T) -> Modifier): Modifier = composed {
    then(if (value != null) other(value) else Modifier)
}

fun Modifier.onEnter(block: () -> Unit) = this.onKeyEvent {
    if (it.key == Key.Enter) {
        block()
        return@onKeyEvent true
    }
    false
}

@Stable
fun Modifier.contentDescription(contentDescription: String?): Modifier {
    return if (contentDescription != null) {
        semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        this
    }
}
