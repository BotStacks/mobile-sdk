package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.utils.ui.addIf
import ai.botstacks.sdk.utils.ui.keyboardAsState
import ai.botstacks.sdk.utils.ui.measured
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.isUnspecified
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextInput2(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 1,
    maxLines: Int = 4,
    state: TextFieldState,
    onStateChanged: () -> Unit = { },
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    fontStyle: FontStyle = BotStacks.fonts.body1,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = BotStacks.colorScheme.onBackground,
    indicatorColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    val lineLimit = if (maxLines == 1) {
        TextFieldLineLimits.SingleLine
    } else {
        TextFieldLineLimits.MultiLine(minHeightInLines = minLines, maxHeightInLines = maxLines)
    }

    var height by remember { mutableStateOf(Dp.Unspecified) }

    BasicTextField2(
        modifier = modifier
            .addIf(height.isSpecified) { Modifier.heightIn(min = height) }
            .measured {
                if (height.isUnspecified) {
                    height = it.height
                }
            },
        enabled = enabled,
        readOnly = readOnly,
        state = state,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = fontStyle.copy(textAlign = textAlign).asTextStyle().copy(color = color),
        lineLimits = lineLimit,
        decorator = { innerTextField ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = when (textAlign) {
                    TextAlign.End -> Alignment.End
                    TextAlign.Center -> Alignment.CenterHorizontally
                    else -> Alignment.Start
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x2)
                ) {
                    leadingIcon?.invoke()
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.text.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = placeholder,
                                fontStyle = BotStacks.fonts.caption1,
                                color = BotStacks.colorScheme.onChatInput,
                                textAlign = textAlign
                            )
                        }
                        innerTextField()
                    }
                    trailingIcon?.invoke()
                }
                if (indicatorColor.isSpecified) {
                    Spacer(modifier = Modifier.height(BotStacks.dimens.grid.x2))
                    Divider(color = indicatorColor)
                }
            }
        },
        scrollState = scrollState
    )

    LaunchedEffect(Unit) {
        state.textAsFlow()
            .onEach { onStateChanged() }
            .launchIn(this)
    }

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SecureTextField2(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    state: TextFieldState,
    onStateChanged: () -> Unit = { },
    fontStyle: FontStyle = BotStacks.fonts.body1,
    color: Color = BotStacks.colorScheme.onBackground,
    textObfuscationMode: TextObfuscationMode = TextObfuscationMode.RevealLastTyped,
    enabled: Boolean = true,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    onSubmit: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    var height by remember { mutableStateOf(Dp.Unspecified) }

    BasicSecureTextField(
        modifier = modifier
            .addIf(height.isSpecified) { Modifier.heightIn(min = height) }
            .measured {
                if (height.isUnspecified) {
                    height = it.height
                }
            },
        enabled = enabled,
        state = state,
        textStyle = fontStyle.asTextStyle().copy(color = color),
        textObfuscationMode = textObfuscationMode,
        onSubmit = {
            if (it == ImeAction.Go) {
                onSubmit()
                return@BasicSecureTextField true
            }
            false
        },
        decorator = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon()
                Box(modifier = Modifier.weight(1f)) {
                    it()
                    if (state.text.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            fontStyle = BotStacks.fonts.caption1,
                            color = BotStacks.colorScheme.onChatInput
                        )
                    }
                }
                trailingIcon()
            }
        },
        scrollState = scrollState
    )

    LaunchedEffect(Unit) {
        state.textAsFlow()
            .onEach { onStateChanged() }
            .launchIn(this)
    }

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}