/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.ui.components.internal

import ai.botstacks.sdk.ui.BotStacks
import ai.botstacks.sdk.ui.components.Text
import ai.botstacks.sdk.ui.theme.FontStyle
import ai.botstacks.sdk.utils.ui.addIf
import ai.botstacks.sdk.utils.ui.keyboardAsState
import ai.botstacks.sdk.utils.ui.measured
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.isUnspecified

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 1,
    maxLines: Int = 4,
    value: String,
    onValueChanged: (String) -> Unit = { },
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
) {
    BasicTextField(
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = fontStyle.copy(textAlign = textAlign).asTextStyle().copy(color = color),
        minLines = minLines,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = when (textAlign) {
                    TextAlign.End -> Alignment.End
                    TextAlign.Center -> Alignment.CenterHorizontally
                    else -> Alignment.Start
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x2)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        leadingIcon?.invoke()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty()) {
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
    )

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    minLines: Int = 1,
    maxLines: Int = 4,
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit = { },
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
) {
    var height by remember { mutableStateOf(Dp.Unspecified) }

    BasicTextField(
        modifier = modifier
            .addIf(height.isSpecified) { Modifier.heightIn(min = height) }
            .measured {
                if (height.isUnspecified) {
                    height = it.height
                }
            },
        enabled = enabled,
        readOnly = readOnly,
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = fontStyle.copy(textAlign = textAlign).asTextStyle().copy(color = color),
        minLines = minLines,
        maxLines = maxLines,
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = when (textAlign) {
                    TextAlign.End -> Alignment.End
                    TextAlign.Center -> Alignment.CenterHorizontally
                    else -> Alignment.Start
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(BotStacks.dimens.grid.x2)
                ) {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center) {
                        leadingIcon?.invoke()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.text.isEmpty()) {
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
    )

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}

@Composable
fun SecureTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    value: String,
    onValueChanged: (String) -> Unit,
    fontStyle: FontStyle = BotStacks.fonts.body1,
    color: Color = BotStacks.colorScheme.onBackground,
    enabled: Boolean = true,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    onSubmit: () -> Unit,
) {
    var height by remember { mutableStateOf(Dp.Unspecified) }

    BasicTextField(
        modifier = modifier
            .addIf(height.isSpecified) { Modifier.heightIn(min = height) }
            .measured {
                if (height.isUnspecified) {
                    height = it.height
                }
            },
        enabled = enabled,
        value = value,
        onValueChange = onValueChanged,
        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
        textStyle = fontStyle.asTextStyle().copy(color = color),
        keyboardActions = KeyboardActions(onGo = { onSubmit() }),
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon()
                Box(modifier = Modifier.weight(1f)) {
                    it()
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
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
    )

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}

@Composable
fun SecureTextField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
    fontStyle: FontStyle = BotStacks.fonts.body1,
    color: Color = BotStacks.colorScheme.onBackground,
    enabled: Boolean = true,
    leadingIcon: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    onSubmit: () -> Unit,
) {
    BasicTextField(
        modifier = modifier,
        enabled = enabled,
        value = value,
        onValueChange = onValueChanged,
        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
        textStyle = fontStyle.asTextStyle().copy(color = color),
        keyboardActions = KeyboardActions(onGo = { onSubmit() }),
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leadingIcon()
                Box(modifier = Modifier.weight(1f)) {
                    it()
                    if (value.text.isEmpty() && placeholder.isNotEmpty()) {
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
    )

    val focusManager = LocalFocusManager.current
    val keyboardState by keyboardAsState()
    LaunchedEffect(keyboardState) {
        if (!keyboardState) {
            focusManager.clearFocus(true)
        }
    }
}

