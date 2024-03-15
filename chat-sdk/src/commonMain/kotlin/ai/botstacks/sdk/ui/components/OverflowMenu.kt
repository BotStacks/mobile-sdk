package ai.botstacks.sdk.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

/**
 * Interface that an [OverflowMenu] renders items based upon. This is utilized in [Header] for the [Header.menu].
 *
 * ```kotlin
 * Header(
 *     [...],
 *     menu = {
 *         item { Text(text = "Item 1") }
 *         label(onClick = {  }) {
 *             Text(text = "Item 2")
 *          }
 *      }
 * )
 * ```
 */
interface OverflowMenuScope {

    /**
     * Adds an item where content is completely provided by caller.
     */
    public fun item(
        content: @Composable (PaddingValues) -> Unit
    )

    /**
     * Creates an item within an [OverflowMenu].
     *
     * @param enabled whether clicks are enabled.
     * @param onClick callback for when this item is clicked.
     * @param icon Optional leading icon slot for this item.
     * @param subtitle Optional subtitle to be displayed under [title].
     * @param title Title for this item.
     *
     */
    public fun label(
        enabled: Boolean = true,
        onClick: () -> Unit,
        icon: (@Composable () -> Unit)? = null,
        subtitle: @Composable () -> Unit = { },
        title: @Composable () -> Unit,
    )
}