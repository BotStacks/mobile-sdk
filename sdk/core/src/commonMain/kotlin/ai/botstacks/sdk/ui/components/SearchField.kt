package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.internal.Pressable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
    showClear: Boolean = false,
    onSearch: () -> Unit = { },
    onClear: () -> Unit = { }
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        decorationBox = {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = "Search...",
                            fontStyle = BotStacks.fonts.caption1,
                            color = BotStacks.colorScheme.onChatInput
                        )
                    }
                    it()
                }
                if (showClear) {
                    Pressable(
                        onClick = {
                            onClear()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.requiredIconSize(),
                            painter = painterResource(Res.drawable.x),
                            contentDescription = "close search"
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun SearchField2(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    showClear: Boolean = false,
    onSearch: () -> Unit = { },
    onClear: () -> Unit = { }
) {
    BasicTextField2(
        modifier = modifier,
        state = state,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        decorator = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    it()
                    if (state.text.isEmpty()) {
                        Text(
                            text = "Search...",
                            fontStyle = BotStacks.fonts.caption1,
                            color = BotStacks.colorScheme.onChatInput
                        )
                    }
                }
                if (showClear) {
                    Pressable(
                        onClick = {
                            state.clearText()
                            onClear()
                        }
                    ) {
                        Icon(
                            modifier = Modifier.requiredIconSize(),
                            painter = painterResource(Res.drawable.x),
                            contentDescription = "close search"
                        )
                    }
                }
            }
        }
    )
}