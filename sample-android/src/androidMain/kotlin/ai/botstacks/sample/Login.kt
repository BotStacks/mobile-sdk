package ai.botstacks.sample

import ai.botstacks.sdk.BotStacksChat
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.theme.LocalBotStacksColorScheme
import ai.botstacks.sdk.ui.theme.LocalBotStacksDayNightColorScheme
import ai.botstacks.sdk.ui.components.Space
import android.Manifest
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.TextField
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
fun isValidEmail(email: String): Boolean {
    return email.matches(emailRegex)
}


@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
)
@Composable
fun Login(openChat: () -> Unit, register: () -> Unit) {
    val scope = rememberCoroutineScope()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                val result = FirebaseMessaging.getInstance().token()
                result.getOrNull()?.let {
                    BotStacksChat.registerFCMToken(it)
                }
            }
        }
        openChat()
    }

    val terms = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    BackHandler(enabled = true) {
        scope.launch { terms.hide() }
    }
    var email by remember { mutableStateOf(TextFieldValue()) }
    var password  by remember { mutableStateOf(TextFieldValue()) }

    val passwordFocus = remember { FocusRequester() }
    val canLogin = isValidEmail(email.text) && password.text.length > 4
    val login = {
        if (canLogin) {
            Log.v("InAppChat-Sample", "Login Click")
            scope.launch {
                BotStacksChat.shared.login(null, "1", "testuser", "testuser", "")
                Log.v("InAppChat-Sample", "Finish login")
                if (BotStacksChat.shared.isUserLoggedIn) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    CompositionLocalProvider(LocalBotStacksColorScheme provides LocalBotStacksDayNightColorScheme.current.night) {
        ModalBottomSheetLayout(
            sheetContent = {
                BrowserModal(url = "https://botstacks.ai/terms-of-service") {
                    scope.launch { terms.hide() }
                }
            }, sheetState = terms
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(colorScheme.background)
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(24.dp)
                    .imePadding()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                item {
                    InAppChatLogo()
                    Space(60f)
                    val autofillEmail =
                        AutoFillRequestHandler(autofillTypes = listOf(AutofillType.EmailAddress),
                            onFill = { email = TextFieldValue(it) }
                        )

                    TextField(
                        modifier = Modifier
                            .connectNode(handler = autofillEmail)
                            .defaultFocusChangeAutoFill(handler = autofillEmail),
                        value = email,
                        onValueChange = { email = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { passwordFocus.captureFocus() }),
                        maxLines = 1,
                    )
                    Space(12f)
                    val autofillPassword =
                        AutoFillRequestHandler(autofillTypes = listOf(AutofillType.Password),
                            onFill = { password = TextFieldValue(it) }
                        )

                    TextField(
                        modifier = Modifier
                            .connectNode(autofillPassword)
                            .defaultFocusChangeAutoFill(autofillPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        value = password,
                        onValueChange = { password = it },
                    )

                    ElevatedButton(
                        onClick = login,
                        enabled = !BotStacksChat.shared.loggingIn && canLogin,
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = colorScheme.primary,
                            contentColor = Color.White
                        ),
                    ) {
                        Text(text = "Login")
                    }
                    Space(4f)
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .clickable {
                                register()
                            }, horizontalArrangement = Arrangement.Center
                    ) {
                        val annotatedString = buildAnnotatedString {
                            append("Don't have an account? ")

                            pushStringAnnotation(
                                tag = "register",
                                annotation = ""
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("Create one")
                            }
                        }
                        Text(
                            annotatedString,
                            color = colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                    Space(16f)
                    Row(
                        modifier = Modifier
                            .clickable {
                                scope.launch { terms.show() }
                            }, horizontalArrangement = Arrangement.Center
                    ) {
                        val annotatedString = buildAnnotatedString {
                            append("By logging in, you agree to the InAppChat ")

                            pushStringAnnotation(
                                tag = "terms of service",
                                annotation = "https://google.com/policy"
                            )
                            withStyle(
                                style = SpanStyle(
                                    color = colorScheme.primary,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("terms of service")
                            }
                        }
                        Text(
                            annotatedString,
                            color = colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private suspend fun FirebaseMessaging.token(): Result<String?> =
    suspendCancellableCoroutine { cont ->
        runCatching {
            FirebaseMessaging.getInstance().token.continueWith<String?> { null }
                .addOnCompleteListener {
                    it.result?.let {
                        cont.resume(Result.success(it))
                    }
                }.addOnFailureListener {
                    Timber.tag("Login").e(it.stackTraceToString())
                    cont.resume(Result.success(null))
                }
        }
    }

@Preview
@Composable
fun LoginPreview() {
    MaterialTheme {
        Login(openChat = {}, register = {})
    }
}