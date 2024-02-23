/*
 * Copyright (c) 2023.
 */

package ai.botstacks.sdk.navigation.ui.profile

import ai.botstacks.sdk.API
import ai.botstacks.sdk.state.Upload
import ai.botstacks.sdk.state.User
import ai.botstacks.sdk.type.UpdateProfileInput
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.EditProfileState
import ai.botstacks.sdk.ui.components.EditProfileView
import ai.botstacks.sdk.ui.components.Header
import ai.botstacks.sdk.ui.components.HeaderDefaults
import ai.botstacks.sdk.ui.components.internal.ProgressOverlay
import ai.botstacks.sdk.ui.components.UserDetailsView
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.genImageMessage
import ai.botstacks.sdk.utils.genU
import ai.botstacks.sdk.utils.random
import ai.botstacks.sdk.utils.ui.keyboardAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.apollographql.apollo3.api.Optional
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ProfileView(
    user: User,
    onBackClicked: () -> Unit,
) {
    val isCurrentUser = remember(user) { user.isCurrent }
    if (isCurrentUser) {
        EditProfileScreen(user = user, onBackClicked = onBackClicked)
    } else {
        UserDetailsScreen(user = user, onBackClicked = onBackClicked)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EditProfileScreen(
    user: User,
    onBackClicked: () -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    val composeScope = rememberCoroutineScope()
    val state = remember(user) { EditProfileState(user) }

    LaunchedEffect(saving) {
        if (saving) {
            val uploadFile = state.selectedImage?.let { Upload(file = it) }
            val url = uploadFile?.await()
            runCatching {
                API.updateProfile(
                    UpdateProfileInput(
                        username = Optional.present(state.textState.text.toString()),
                        image = Optional.presentIfNotNull(url)
                    )
                )

                User.current?.apply {
                    if (url != null) {
                        avatar = url
                    }
                    username = state.textState.text.toString()
                }
            }.onFailure {
                saving = false
            }.onSuccess {
                saving = false
                onBackClicked()
            }
        }
    }

    val keyboardVisible by keyboardAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Header(
                title = "Edit Profile",
                onBackClicked = onBackClicked,
                endAction = {
                    HeaderDefaults.SaveAction {
                        composeScope.launch {
                            if (keyboardVisible) {
                                keyboardController?.hide()
                                delay(300.milliseconds)
                            }
                            saving = true
                        }
                    }
                }
            )

            EditProfileView(state = state)
        }
        ProgressOverlay(visible = saving, touchBlocking = true)
    }
}

@Composable
private fun UserDetailsScreen(user: User, onBackClicked: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Header(
                title = "User Details",
                onBackClicked = onBackClicked,
            )

            UserDetailsView(user)
        }
    }
}

@IPreviews
@Composable
fun ProfileViewPreview() {
    val u = genU()
    val ms = random(10, { genImageMessage(user = u) })
//    u.sharedMedia.items.addAll(ms)
    BotStacksChatContext {
        ProfileView(user = u, onBackClicked = { /*TODO*/ })
    }
}