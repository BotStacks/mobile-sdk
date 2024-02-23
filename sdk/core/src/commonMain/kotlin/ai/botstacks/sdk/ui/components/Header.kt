/*
 * Copyright (c) 2023.
 */

@file:OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)

package ai.botstacks.sdk.ui.components

import ai.botstacks.sdk.type.OnlineStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ai.botstacks.sdk.ui.BotStacks.colorScheme
import ai.botstacks.sdk.ui.BotStacks.fonts
import ai.botstacks.sdk.ui.BotStacksChatContext
import ai.botstacks.sdk.ui.components.internal.OverflowMenu
import ai.botstacks.sdk.ui.components.internal.OverflowMenuScope
import ai.botstacks.sdk.ui.components.internal.Pressable
import ai.botstacks.sdk.ui.resources.botstacks_logo_daynight
import ai.botstacks.sdk.utils.Fn
import ai.botstacks.sdk.utils.IPreviews
import ai.botstacks.sdk.utils.annotated
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import botstacks.sdk.core.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

val HeaderHeight = 56.dp

object HeaderDefaults {
    @Composable
    fun Title(text: String) {
        Text(
            text = text.annotated(),
            fontStyle = fonts.h2,
            color = colorScheme.onBackground,
            maxLines = 1
        )
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun Logo() {
        Icon(
            painter = painterResource(resource = Res.drawable.botstacks_logo_daynight),
            tint = Color.Unspecified,
            contentDescription = "logo"
        )
    }

    @Composable
    fun NextAction(onClick: Fn) {
        Pressable(onClick = onClick) {
            Text(
                text = "Next",
                fontStyle = fonts.button1,
                color = colorScheme.primary,
            )
        }
    }

    @Composable
    fun SaveAction(onClick: Fn) {
        Pressable(onClick = onClick) {
            Text(
                text = "Save",
                fontStyle = fonts.button1,
                color = colorScheme.primary,
            )
        }
    }

    @Composable
    fun CreateAction(onClick: Fn) {
        Pressable(onClick = onClick) {
            Text(
                text = "Create",
                fontStyle = fonts.button1,
                color = colorScheme.primary,
            )
        }
    }

    @Composable
    fun MenuAction(onClick: Fn) {
        HeaderButton(onClick = onClick) {
            Icon(
                Icons.Outlined.MoreVert,
                contentDescription = "Menu",
                tint = colorScheme.onBackground,
                modifier = Modifier.requiredIconSize()
            )
        }
    }

    val IconSize: Dp = 20.dp
}

internal fun Modifier.requiredIconSize() = this.size(HeaderDefaults.IconSize)

class HeaderState(
    val showSearch: Boolean = false,
    val showSearchClear: Boolean = false,
    isSearchActive: Boolean = false,
) {
    var searchQuery: TextFieldValue by mutableStateOf(TextFieldValue())
    var searchActive by mutableStateOf(isSearchActive)
}

@Composable
fun rememberHeaderState(
    isSearchVisible: Boolean = false,
    isSearchActive: Boolean = false,
    showSearchClear: Boolean = false,
) = remember(isSearchVisible, showSearchClear) {
    HeaderState(isSearchVisible, isSearchActive, showSearchClear)
}
@Composable
fun Header(
    title: String,
    icon: @Composable Fn = { },
    state: HeaderState = rememberHeaderState(),
    onSearchClick: Fn? = null,
    onAdd: Fn? = null,
    onCompose: Fn? = null,
    onBackClicked: Fn? = null,
    menu: (OverflowMenuScope.() -> Unit)? = null,
    endAction: @Composable Fn = { },
) {
    Header(
        title = { HeaderDefaults.Title(text = title) },
        icon = icon,
        state = state,
        onSearchClick = onSearchClick,
        onAdd = onAdd,
        onCompose = onCompose,
        onBackClick = onBackClicked,
        menu = menu,
        endAction = endAction,
    )
}


@Composable
fun Header() {
    Header(icon = { HeaderDefaults.Logo() })
}

@OptIn(
    ExperimentalAnimationApi::class
)
@Composable
fun Header(
    title: @Composable Fn = { },
    icon: @Composable Fn = { },
    state: HeaderState = rememberHeaderState(),
    onSearchClick: Fn? = null,
    onAdd: Fn? = null,
    onCompose: Fn? = null,
    onBackClick: Fn? = null,
    menu: (OverflowMenuScope.() -> Unit)? = null,
    endAction: @Composable Fn = { },
) {
    Row(
        modifier = Modifier
            .background(colorScheme.header)
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                vertical = 4.dp,
                horizontal = 16.dp
            )
            .heightIn(min = HeaderHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (onBackClick != null) {
            HeaderButton(
                onBackClick,
                true
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.ArrowBack),
                    contentDescription = "back",
                    modifier = Modifier.size(24.dp),
                    tint = colorScheme.onBackground,
                )
            }
        }

        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = state.searchActive,
            label = "",
            transitionSpec = {
                fadeIn() togetherWith  fadeOut()
            }
        ) { active ->
            if (active) {
                val focusRequester = remember { FocusRequester() }
                SearchField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = state.searchQuery,
                    onValueChanged = { state.searchQuery = it },
                    showClear = state.showSearchClear,
                    onClear = { state.searchQuery = TextFieldValue() }
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                ) {
                    icon()
                    title()
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                    ) {

                        if (state.showSearch) {
                            HeaderButton(onClick = onSearchClick?.let { { it.invoke() } } ?: { state.searchActive = true }) {
                                Icon(
                                    painterResource(Res.drawable.magnifying_glass),
                                    contentDescription = "Search",
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.requiredIconSize()
                                )
                            }
                        }
                        if (onAdd != null) {
                            HeaderButton(onAdd) {
                                Icon(
                                    painterResource(Res.drawable.plus),
                                    contentDescription = "Add",
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.requiredIconSize()
                                )
                            }
                        }
                        if (onCompose != null) {
                            HeaderButton(onClick = onCompose) {
                                Icon(
                                    painterResource(Res.drawable.edit_outlined),
                                    contentDescription = "Compose",
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.requiredIconSize()
                                )
                            }
                        }
                        if (menu != null) {
                            var expanded by remember {
                                mutableStateOf(false)
                            }
                            OverflowMenu(
                                visible = expanded,
                                onDismissRequest = { expanded = false },
                                menu = { menu(this) },
                            ) {
                                HeaderButton(onClick = { expanded = true }) {
                                    Icon(
                                        Icons.Outlined.MoreVert,
                                        contentDescription = "Menu",
                                        tint = colorScheme.onBackground,
                                        modifier = Modifier.requiredIconSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        endAction()
    }
}

@Composable
fun HeaderButton(onClick: Fn, transparent: Boolean = false, icon: @Composable Fn) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(0.dp)
            .size(30.dp)
            .background(
                if (transparent) Color.Transparent else colorScheme.header,
                CircleShape
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(0.dp, Color.Transparent, CircleShape)
    ) {
        icon()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@IPreviews
@Composable
fun HeaderPreviews() {
    BotStacksChatContext {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Header(
                icon = { HeaderDefaults.Logo() },
                state = rememberHeaderState(isSearchVisible = true),
                onCompose = {},
                menu = {
                    label(onClick = {}) {
                        Text(text = "Log out", fontStyle = fonts.body1, color = colorScheme.error)
                    }
                }
            )
            Header(
                title = "Thread",
                onBackClicked = {},
                menu = {
                    label(onClick = {}) {
                        Text(text = "Log out", fontStyle = fonts.body1, color = colorScheme.error)
                    }
                }
            )
            Header(title = "Create Channel",
                onBackClicked = {},
                endAction = {
                    HeaderDefaults.NextAction {}
                }
            )
            Header(
                icon = {
                    Avatar(
                        modifier = Modifier.padding(vertical = 5.dp),
                        size = AvatarSize.Custom(46.dp),
                        url = null,
                        status = OnlineStatus.Online
                    )
                },
                onBackClicked = {},
                title = "Albert Bell",
                menu = {
                    label(onClick = {}) {
                        Text(text = "Log out", fontStyle = fonts.body1, color = colorScheme.error)
                    }
                }
            )

            Header(
                onBackClicked = {},
                title = "Users",
                state = rememberHeaderState(isSearchVisible = true)
            )
        }
    }
}